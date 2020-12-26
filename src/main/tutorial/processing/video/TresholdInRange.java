package main.tutorial.processing.video;

import main.frames.SliderHSVPanel;
import main.misc.Util;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TresholdInRange {
    private static final String WINDOW_NAME = "Thresholding Operations using inRange demo";

    private static VideoCapture cap;
    private static JFrame frame;
    private static JLabel imgCaptureLabel;
    private static JLabel imgDetectionLabel;
    private static CaptureTask captureTask;
    private static final SliderHSVPanel sliderPanel = new SliderHSVPanel();

    public static void run() {
        final Mat matFrame = new Mat();
        cap = new VideoCapture(0);
        cap.read(matFrame);

        // Create and set up the window.
        frame = new JFrame(WINDOW_NAME);
        initFrame(frame.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                TresholdInRange.captureTask.cancel(true);
            }
        });
        // Set up the content pane.
        final Image img = HighGui.toBufferedImage(matFrame);
        addComponentsToPane(frame.getContentPane(), img);

        frame.pack();
        frame.setVisible(true);

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static void initFrame(final Container pane) {
        pane.add(sliderPanel.getPanel(), BorderLayout.PAGE_START);
    }

    private static class CaptureTask extends SwingWorker<Void, Mat> {
        @Override
        protected Void doInBackground() {
            final Mat matFrame = new Mat();
            while (!isCancelled()) {
                if (!TresholdInRange.cap.read(matFrame)) {
                    break;
                }
                publish(matFrame.clone());
            }
            return null;
        }

        @Override
        protected void process(final List<Mat> frames) {
            final Mat frame = frames.get(frames.size() - 1);
            final Mat frameHSV = new Mat();
            Imgproc.cvtColor(frame, frameHSV, Imgproc.COLOR_BGR2HSV);
            final Mat thresh = new Mat();
            final Scalar lowerb = new Scalar(TresholdInRange.sliderPanel.getSliderLowHValue(), TresholdInRange.sliderPanel.getSliderLowSValue(), TresholdInRange.sliderPanel.getsliderLowVValue());
            final Scalar upperb = new Scalar(TresholdInRange.sliderPanel.getSliderHighHValue(), TresholdInRange.sliderPanel.getsliderHighSValue(), TresholdInRange.sliderPanel.getsliderHighVValue());
            Core.inRange(frameHSV, lowerb, upperb, thresh);
            update(frame, thresh);
        }

        private static void update(final Mat imgCapture, final Mat imgThresh) {
            TresholdInRange.imgCaptureLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(imgCapture)));
            TresholdInRange.imgDetectionLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(imgThresh)));
            TresholdInRange.frame.repaint();
        }

    }

    private static void addComponentsToPane(final Container pane, final Image img) {
        final JPanel framePanel = new JPanel();
        imgCaptureLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgCaptureLabel);
        imgDetectionLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgDetectionLabel);
        pane.add(framePanel, BorderLayout.CENTER);
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(TresholdInRange::run);
    }
}
