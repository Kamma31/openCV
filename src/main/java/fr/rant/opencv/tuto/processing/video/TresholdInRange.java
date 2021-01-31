package fr.rant.opencv.tuto.processing.video;

import fr.rant.opencv.Util;
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
    private static JFrame frame;
    private static JLabel imgCaptureLabel;
    private static JLabel imgDetectionLabel;
    private static VideoCapture cap;
    private static CaptureTask captureTask;
    private static JSlider sliderLowH;
    private static JSlider sliderHighH;
    private static JSlider sliderLowS;
    private static JSlider sliderHighS;
    private static JSlider sliderLowV;
    private static JSlider sliderHighV;

    public static void run() {
        final Mat matFrame = new Mat();
        cap = new VideoCapture(0);
        cap.read(matFrame);

        initMainFrame(matFrame);

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static void initMainFrame(final Mat matFrame) {
        // Create and set up the window.
        frame = new JFrame(WINDOW_NAME);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                TresholdInRange.captureTask.cancel(true);
            }
        });
        // Set up the content pane.
        final Image img = HighGui.toBufferedImage(matFrame);

        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.add(new JLabel("Low hue"), BorderLayout.CENTER);
        sliderLowH = new JSlider(0, 360, 0);
        sliderLowH.setMajorTickSpacing(50);
        sliderLowH.setMinorTickSpacing(10);
        sliderLowH.setPaintTicks(true);
        sliderLowH.setPaintLabels(true);
        sliderPanel.add(sliderLowH, BorderLayout.CENTER);
        sliderPanel.add(new JLabel("High hue"), BorderLayout.CENTER);
        sliderHighH = new JSlider(0, 360, 360);
        sliderHighH.setMajorTickSpacing(50);
        sliderHighH.setMinorTickSpacing(10);
        sliderHighH.setPaintTicks(true);
        sliderHighH.setPaintLabels(true);
        sliderPanel.add(sliderHighH, BorderLayout.CENTER);
        sliderPanel.add(new JLabel("Low saturation"), BorderLayout.CENTER);
        sliderLowS = new JSlider(0, 100, 0);
        sliderLowS.setMajorTickSpacing(50);
        sliderLowS.setMinorTickSpacing(10);
        sliderLowS.setPaintTicks(true);
        sliderLowS.setPaintLabels(true);
        sliderPanel.add(sliderLowS, BorderLayout.CENTER);
        sliderPanel.add(new JLabel("High saturation"), BorderLayout.CENTER);
        sliderHighS = new JSlider(0, 100, 100);
        sliderHighS.setMajorTickSpacing(50);
        sliderHighS.setMinorTickSpacing(10);
        sliderHighS.setPaintTicks(true);
        sliderHighS.setPaintLabels(true);
        sliderPanel.add(sliderHighS, BorderLayout.CENTER);
        sliderPanel.add(new JLabel("Low value"), BorderLayout.CENTER);
        sliderLowV = new JSlider(0, 100, 0);
        sliderLowV.setMajorTickSpacing(50);
        sliderLowV.setMinorTickSpacing(10);
        sliderLowV.setPaintTicks(true);
        sliderLowV.setPaintLabels(true);
        sliderPanel.add(sliderLowV, BorderLayout.CENTER);
        sliderPanel.add(new JLabel("High value"), BorderLayout.CENTER);
        sliderHighV = new JSlider(0, 100, 100);
        sliderHighV.setMajorTickSpacing(50);
        sliderHighV.setMinorTickSpacing(10);
        sliderHighV.setPaintTicks(true);
        sliderHighV.setPaintLabels(true);
        sliderPanel.add(sliderHighV, BorderLayout.CENTER);
        sliderLowH.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valH = Math.min(sliderHighH.getValue() - 1, source.getValue());
            sliderLowH.setValue(valH);
        });
        sliderHighH.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valH = Math.max(source.getValue(), sliderLowH.getValue() + 1);
            sliderHighH.setValue(valH);
        });
        sliderLowS.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valS = Math.min(sliderHighS.getValue() - 1, source.getValue());
            sliderLowS.setValue(valS);
        });
        sliderHighS.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valS = Math.max(source.getValue(), sliderLowS.getValue() + 1);
            sliderHighS.setValue(valS);
        });
        sliderLowV.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valV = Math.min(sliderHighV.getValue() - 1, source.getValue());
            sliderLowV.setValue(valV);
        });
        sliderHighV.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valV = Math.max(source.getValue(), sliderLowV.getValue() + 1);
            sliderHighV.setValue(valV);
        });

        frame.add(sliderPanel, BorderLayout.PAGE_START);
        final JPanel framePanel = new JPanel();
        imgCaptureLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgCaptureLabel);
        imgDetectionLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgDetectionLabel);
        frame.add(framePanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
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
            final Scalar lowerb = new Scalar(sliderLowH.getValue(), sliderLowS.getValue(), sliderLowV.getValue());
            final Scalar upperb = new Scalar(sliderHighH.getValue(), sliderHighS.getValue(), sliderHighV.getValue());
            Core.inRange(frameHSV, lowerb, upperb, thresh);
            update(frame, thresh);
        }

        private static void update(final Mat imgCapture, final Mat imgThresh) {
            TresholdInRange.imgCaptureLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(imgCapture)));
            TresholdInRange.imgDetectionLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(imgThresh)));
            TresholdInRange.frame.repaint();
        }
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(TresholdInRange::run);
    }

}


