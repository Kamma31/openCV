package fr.rant.opencv.tuto.processing.images;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import javax.swing.*;

import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;

public class FaceDetection {
    public static void run() {
        // Create a face detector from the cascade file in the resources directory.
        final Mat img;
        final RectVector faceDetections;
        try (final CascadeClassifier faceDetector = new CascadeClassifier(Util.getResource("xml/lbpcascade_frontalface.xml"))) {
            img = Util.getMatResource("lena.png");

            // Detect faces in the image.
            // MatOfRect is a special container class for Rect.
            faceDetections = new RectVector();
            faceDetector.detectMultiScale(img, faceDetections);
        }
        // Draw a bounding box around each face.
        for (final Rect rect : faceDetections.get()) {
            rectangle(img, new Point(rect.x(), rect.y()), new Point(rect.x() + rect.width(), rect.y() + rect.height()), AbstractScalar.GREEN);
        }

        final JFrame frame = new JFrame("Face detection");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JLabel label = new JLabel();
        label.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(img)));

        frame.add(label);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(FaceDetection::run);
    }
}