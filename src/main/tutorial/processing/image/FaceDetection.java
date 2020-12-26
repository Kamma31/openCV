package main.tutorial.processing.image;

import main.misc.Tasks;
import main.misc.Util;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetection {
    public static void run() {
        // Create a face detector from the cascade file in the resources directory.
        CascadeClassifier faceDetector = new CascadeClassifier(Util.getResource("xml/lbpcascade_frontalface.xml"));
        Mat img = Util.getMatResource("lena.png");

        // Detect faces in the image.
        // MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(img, faceDetections);
        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }

        HighGui.namedWindow(Tasks.FACE_DETECTION.getName(), HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(Tasks.FACE_DETECTION.getName(), img);
        HighGui.moveWindow(Tasks.FACE_DETECTION.getName(), 400, 400);
        HighGui.waitKey(1);
    }

    public static void main(String[] args) {
        Util.loadLibrairies();
        run();
        System.exit(0);
    }
}