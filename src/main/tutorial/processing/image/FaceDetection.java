package main.tutorial.processing.image;

import main.misc.Util;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetection {
    private static final String WINDOWS_NAME = "Face detection";

    public static void run() {
        // Create a face detector from the cascade file in the resources directory.
        final CascadeClassifier faceDetector = new CascadeClassifier(Util.getResource("xml/lbpcascade_frontalface.xml"));
        final Mat img = Util.getMatResource("lena.png");

        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        final MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(img, faceDetections);
        // Draw a bounding box around each face.
        for (final Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }

        HighGui.namedWindow(WINDOWS_NAME, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(WINDOWS_NAME, img);
        HighGui.moveWindow(WINDOWS_NAME, 400, 400);
        HighGui.waitKey(1);
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        run();
        System.exit(0);
    }
}