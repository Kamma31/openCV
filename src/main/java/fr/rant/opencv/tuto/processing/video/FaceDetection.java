package fr.rant.opencv.tuto.processing.video;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.opencv_core.AbstractScalar.GREEN;

public class FaceDetection {
    private static FrameGrabber frameGrabber;
    private static final OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
    private static CaptureTask captureTask;
    private static JLabel label;
    private static JFrame frame;

    public static void run() {
        Mat matFrame = new Mat();
        try {
            frameGrabber = FrameGrabber.createDefault(0);
            frameGrabber.start();
            matFrame = converter.convert(frameGrabber.grab());
        } catch (final FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Face detection");
        label = new JLabel(new ImageIcon(Java2DFrameUtils.toBufferedImage(matFrame)));
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
        protected Void doInBackground() throws FrameGrabber.Exception {
            final CascadeClassifier faceClassifier = new CascadeClassifier();
            faceClassifier.load(Util.getResource("xml/lbpcascade_frontalface.xml"));
            classifiers = new CascadeClassifier[]{faceClassifier};

            while (!isCancelled()) {
                publish(converter.convert(frameGrabber.grab()).clone());
            }
            return null;
        }

        @Override
        protected void process(final List<Mat> images) {
            final Mat img = images.get(images.size() - 1);
            final Mat grayImg = new Mat();
            cvtColor(img, grayImg, COLOR_BGR2GRAY);
            equalizeHist(grayImg, grayImg);

            for (final CascadeClassifier classifier : classifiers) {
                final RectVector mat = new RectVector();
                classifier.detectMultiScale(grayImg, mat);
                Arrays.stream(mat.get()).parallel().forEach(detected -> {
                    final Point center = new Point(detected.x() + detected.width() / 2, detected.y() + detected.height() / 2);
                    ellipse(img, center, new Size(detected.width() / 2, detected.height() / 2), 0, 0, 360,
                            GREEN);
                });
            }
            label.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(img)));
            frame.repaint();
        }
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(FaceDetection::run);
    }
}
