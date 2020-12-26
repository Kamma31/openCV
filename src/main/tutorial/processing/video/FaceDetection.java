package main.tutorial.processing.video;

import main.misc.Util;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.List;

public class FaceDetection {
    public static void main(String[] args) {
        Util.loadLibrairies();
        run();
    }

    private static void run() {
        int cameraDevice =  0;
        CascadeClassifier faceClassifier = new CascadeClassifier();
        CascadeClassifier profileClassifier = new CascadeClassifier();
        faceClassifier.load(Util.getResource("xml/lbpcascade_frontalface.xml"));
        profileClassifier.load(Util.getResource("xml/haarcascade_profileface.xml"));

        VideoCapture capture = new VideoCapture(cameraDevice);
        if (!capture.isOpened()) {
            System.exit(0);
        }
        Mat frame = new Mat();
        while (capture.read(frame)) {
            if (frame.empty()) {
                break;
            }
            //-- 3. Apply the classifier to the frame
            detectAndDisplay(frame, new CascadeClassifier[]{faceClassifier, profileClassifier});
            if (HighGui.waitKey(10) == 27) {
                break;// escape
            }
        }
        System.exit(0);
    }

    public static void detectAndDisplay(Mat frame, CascadeClassifier[] classifiers ) {
        Mat frameGray = new Mat();
        Imgproc.cvtColor(frame, frameGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(frameGray, frameGray);

        for (CascadeClassifier classifier : classifiers) {
            MatOfRect mat = new MatOfRect();
            classifier.detectMultiScale(frameGray, mat);
            List<Rect> listOfDetected = mat.toList();
            listOfDetected.forEach(detected->{
                Point center = new Point(detected.x + detected.width / 2d, detected.y + detected.height / 2d);
                Imgproc.ellipse(frame, center, new Size(detected.width / 2d, detected.height / 2d), 0, 0, 360,
                        new Scalar(255, 0, 255));
            });
        }
        //-- Show what you got
        HighGui.imshow("Capture - Face detection", frame );
    }
}
