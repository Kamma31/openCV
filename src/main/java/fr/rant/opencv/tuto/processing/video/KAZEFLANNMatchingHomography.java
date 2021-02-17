package fr.rant.opencv.tuto.processing.video;

import fr.rant.opencv.Util;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KAZE;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.bytedeco.opencv.global.opencv_features2d.NOT_DRAW_SINGLE_POINTS;

public class KAZEFLANNMatchingHomography {
    private static Mat src;
    private static VideoCapture cap;
    private static CaptureTask captureTask;
    private static JFrame frame;
    private static JLabel resultLabel;

    public static void run() {
        src = new Mat();
        cap = new VideoCapture();
        cap.open(0);
        cap.read(src);
        initFrame();

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static void initFrame() {
        frame = new JFrame();

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                captureTask.cancel(true);
            }
        });

        resultLabel = new JLabel();
        frame.add(resultLabel);


        frame.pack();
        frame.setVisible(true);
    }

    private static class CaptureTask extends SwingWorker<Void, Mat> {
        static final Scalar GREEN = new Scalar(0, 255, 0);

        private Mat objectImg;
        final Mat descriptorsObject = new Mat();
        final KAZE detector = KAZE.create(false, false);
        final DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        Mat objCorners;
        MatOfKeyPoint keypointsObject;

        @Override
        protected Void doInBackground() {
            objectImg = Util.getMatResourceOld("livre_biere.jpg");

            keypointsObject = new MatOfKeyPoint();
            detector.detectAndCompute(objectImg, new Mat(), keypointsObject, descriptorsObject);

            objCorners = new Mat(4, 1, CvType.CV_32FC2);
            final float[] objCornersData = new float[(int) (objCorners.total() * objCorners.channels())];
            objCorners.get(0, 0, objCornersData);
            objCornersData[0] = 0;
            objCornersData[1] = 0;
            objCornersData[2] = objectImg.cols();
            objCornersData[3] = 0;
            objCornersData[4] = objectImg.cols();
            objCornersData[5] = objectImg.rows();
            objCornersData[6] = 0;
            objCornersData[7] = objectImg.rows();
            objCorners.put(0, 0, objCornersData);

            final Mat matFrame = new Mat();
            while (!isCancelled()) {
                if (!cap.read(matFrame)) {
                    break;
                }
                publish(matFrame.clone());
            }
            return null;
        }

        @Override
        protected void process(final List<Mat> frames) {
            src = frames.get(frames.size() - 1);

            //-- Step 1: Detect the keypoints using ORB Detector, compute the descriptors
            final MatOfKeyPoint keypointsScene = new MatOfKeyPoint();
            final Mat descriptorsScene = new Mat();
            detector.detectAndCompute(src, new Mat(), keypointsScene, descriptorsScene);

            //-- Step 2: Matching descriptor vectors with a FLANN based matcher
            final List<MatOfDMatch> knnMatches = new ArrayList<>();
            matcher.knnMatch(descriptorsObject, descriptorsScene, knnMatches, 3);
            //-- Filter matches using the Lowe's ratio test
            final float ratioThresh = 0.7f;
            final List<DMatch> listOfGoodMatches = knnMatches.parallelStream()
                    .filter(knnMatch -> knnMatch.rows() > 1)
                    .map(MatOfDMatch::toArray)
                    .filter(matches -> matches[0].distance < ratioThresh * matches[1].distance)
                    .map(matches -> matches[0])
                    .collect(Collectors.toList());
            final MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(listOfGoodMatches);

            //-- Draw matches
            final Mat imgMatches = new Mat();
            Features2d.drawMatches(objectImg, keypointsObject, src, keypointsScene, goodMatches, imgMatches, GREEN,
                    GREEN, new MatOfByte(), NOT_DRAW_SINGLE_POINTS
            );
            //-- Localize the object
            final List<Point> obj = new ArrayList<>();
            final List<Point> scene = new ArrayList<>();
            final List<KeyPoint> listOfKeypointsObject = keypointsObject.toList();
            final List<KeyPoint> listOfKeypointsScene = keypointsScene.toList();
            for (final DMatch listOfGoodMatch : listOfGoodMatches) {
                //-- Get the keypoints from the good matches
                obj.add(listOfKeypointsObject.get(listOfGoodMatch.queryIdx).pt);
                scene.add(listOfKeypointsScene.get(listOfGoodMatch.trainIdx).pt);
            }
            final MatOfPoint2f objMat = new MatOfPoint2f();
            final MatOfPoint2f sceneMat = new MatOfPoint2f();
            objMat.fromList(obj);
            sceneMat.fromList(scene);
            final double ransacReprojThreshold = 3.0;
            if (sceneMat.empty()) {
                return;
            }
            final Mat homography = Calib3d.findHomography(objMat, sceneMat, Calib3d.RANSAC, ransacReprojThreshold);
            if (homography.empty()) {
                return;
            }
            final Mat sceneCorners = new Mat();
            Core.perspectiveTransform(objCorners, sceneCorners, homography);
            final float[] sceneCornersData = new float[(int) (sceneCorners.total() * sceneCorners.channels())];
            sceneCorners.get(0, 0, sceneCornersData);
            //-- Draw lines between the corners (the mapped object in the scene - image_2 )
            final Point pt1 = new Point(sceneCornersData[0] + objectImg.cols(), sceneCornersData[1]);
            final Point pt2 = new Point(sceneCornersData[2] + objectImg.cols(), sceneCornersData[3]);
            final Point pt3 = new Point(sceneCornersData[4] + objectImg.cols(), sceneCornersData[5]);
            final Point pt4 = new Point(sceneCornersData[6] + objectImg.cols(), sceneCornersData[7]);
            Imgproc.line(imgMatches, pt1, pt2, GREEN, 3);
            Imgproc.line(imgMatches, pt2, pt3, GREEN, 3);
            Imgproc.line(imgMatches, pt3, pt4, GREEN, 3);
            Imgproc.line(imgMatches, pt4, pt1, GREEN, 3);

            resultLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(imgMatches)));
            frame.pack();
            frame.repaint();
        }
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(KAZEFLANNMatchingHomography::run);
    }
}
