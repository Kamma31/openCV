package fr.rant.opencv.tuto.processing.images;

import fr.rant.opencv.Util;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;

public class FaceDetection {
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

        final JFrame frame = new JFrame("Face detection");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel label = new JLabel();
        label.setIcon(new ImageIcon(HighGui.toBufferedImage(img)));

        frame.add(label);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(FaceDetection::run);
    }
}