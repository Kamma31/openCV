package fr.rant.opencv.tuto.processing.video;

import fr.rant.opencv.Util;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class FaceDetection {
    private static VideoCapture cap;
    private static CaptureTask captureTask;
    private static JLabel label;
    private static JFrame frame;

    public static void run() {
        final Mat matFrame = new Mat();
        cap = new VideoCapture();
        cap.open(0);
        cap.read(matFrame);

        frame = new JFrame("Face detection");
        label = new JLabel(new ImageIcon(HighGui.toBufferedImage(matFrame)));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                captureTask.cancel(true);
            }
        });
        frame.add(label);

        frame.pack();
        frame.setVisible(true);

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static class CaptureTask extends SwingWorker<Void, Mat> {
        private CascadeClassifier[] classifiers;

        @Override
        protected Void doInBackground() {
            final CascadeClassifier faceClassifier = new CascadeClassifier();
//            final CascadeClassifier profileClassifier = new CascadeClassifier();
            faceClassifier.load(Util.getResource("xml/lbpcascade_frontalface.xml"));
//            profileClassifier.load(Util.getResource("xml/haarcascade_profileface.xml"));
            classifiers = new CascadeClassifier[]{faceClassifier};//, profileClassifier};
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
        protected void process(final List<Mat> images) {
            final Mat img = images.get(images.size() - 1);
            final Mat grayImg = new Mat();
            Imgproc.cvtColor(img, grayImg, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(grayImg, grayImg);

            for (final CascadeClassifier classifier : classifiers) {
                final MatOfRect mat = new MatOfRect();
                classifier.detectMultiScale(grayImg, mat);
                final List<Rect> listOfDetected = mat.toList();
                listOfDetected.forEach(detected -> {
                    final Point center = new Point(detected.x + detected.width / 2d, detected.y + detected.height / 2d);
                    Imgproc.ellipse(img, center, new Size(detected.width / 2d, detected.height / 2d), 0, 0, 360,
                            new Scalar(255, 0, 255));
                });
            }
            label.setIcon(new ImageIcon(HighGui.toBufferedImage(img)));
            frame.repaint();
        }
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(FaceDetection::run);
    }
}
