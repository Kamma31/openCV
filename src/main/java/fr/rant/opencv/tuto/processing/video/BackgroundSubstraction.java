package fr.rant.opencv.tuto.processing.video;

import fr.rant.opencv.Util;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class BackgroundSubstraction {
    private static Mat src;
    private static VideoCapture cap;
    private static CaptureTask captureTask;
    private static JFrame frame;
    private static JLabel imgCaptureLabel;
    private static JCheckBox substrator;
    private static BackgroundSubtractor backSubMOG2;
    private static BackgroundSubtractor backSubKNN;

    public static void run() {
        src = new Mat();
        cap = new VideoCapture();
        cap.open(0);
        cap.read(src);
        backSubMOG2 = Video.createBackgroundSubtractorMOG2();
        backSubKNN = Video.createBackgroundSubtractorKNN();


        frame = new JFrame("Background Substraction");
        initFrame(frame.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                BackgroundSubstraction.captureTask.cancel(true);
            }
        });

        final Image imgSrc = HighGui.toBufferedImage(src);
        final JPanel framePanel = new JPanel();
        imgCaptureLabel = new JLabel(new ImageIcon(imgSrc));
        framePanel.add(imgCaptureLabel);
        frame.add(framePanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static void initFrame(final Container contentPane) {
        final JPanel panel = new JPanel();
        substrator = new JCheckBox("Use MOG2 (else KNN)");
        panel.add(substrator);

        contentPane.add(panel, BorderLayout.NORTH);
    }

    private static class CaptureTask extends SwingWorker<Void, Mat> {
        static Mat fgMask;

        @Override
        protected Void doInBackground() {
            fgMask = new Mat();
            final Mat matFrame = new Mat();
            while (!isCancelled()) {
                if (!BackgroundSubstraction.cap.read(matFrame)) {
                    break;
                }
                publish(matFrame.clone());
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
            imgCaptureLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(fgMask)));
            frame.repaint();
        }
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(BackgroundSubstraction::run);
    }
}
