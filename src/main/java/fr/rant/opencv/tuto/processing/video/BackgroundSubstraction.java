package fr.rant.opencv.tuto.processing.video;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_video.BackgroundSubtractor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_video.createBackgroundSubtractorKNN;
import static org.bytedeco.opencv.global.opencv_video.createBackgroundSubtractorMOG2;

public class BackgroundSubstraction {
    private static Mat src;
    private static FrameGrabber frameGrabber;
    private static CaptureTask captureTask;
    private static JFrame frame;
    private static JLabel imgCaptureLabel;
    private static JCheckBox substrator;
    private static BackgroundSubtractor backSubMOG2;
    private static BackgroundSubtractor backSubKNN;
    private static final OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

    public static void run() {
        try {
            frameGrabber = FrameGrabber.createDefault(0);
            frameGrabber.start();
            src = converter.convert(frameGrabber.grab());
        } catch (final FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        backSubMOG2 = createBackgroundSubtractorMOG2();
        backSubKNN = createBackgroundSubtractorKNN();

        initFrame();

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static void initFrame() {
        frame = new JFrame("Background Substraction");

        final JPanel panel = new JPanel();
        substrator = new JCheckBox("Use MOG2 (else KNN)");
        panel.add(substrator);

        frame.add(panel, BorderLayout.NORTH);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                BackgroundSubstraction.captureTask.cancel(true);
            }
        });

        final JPanel framePanel = new JPanel();
        imgCaptureLabel = new JLabel(new ImageIcon(Java2DFrameUtils.toBufferedImage(src)));
        framePanel.add(imgCaptureLabel);
        frame.add(framePanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private static class CaptureTask
            extends SwingWorker<Void, Mat> {
        static Mat fgMask;

        @Override
        protected Void doInBackground() throws FrameGrabber.Exception {
            fgMask = new Mat();
            while (!isCancelled()) {
                publish(converter.convert(frameGrabber.grab()).clone());
            }
            return null;
        }

        @Override
        protected void process(final List<Mat> frames) {
            src = frames.get(frames.size() - 1);

            final BackgroundSubtractor backgroundSubtractor;
            if (substrator.isSelected()) {
                backgroundSubtractor = backSubMOG2;
            } else {
                backgroundSubtractor = backSubKNN;
            }

            backgroundSubtractor.apply(src, fgMask);

            update();
        }

        private static void update() {
            imgCaptureLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(fgMask)));
            frame.repaint();
        }
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(BackgroundSubstraction::run);
    }
}
