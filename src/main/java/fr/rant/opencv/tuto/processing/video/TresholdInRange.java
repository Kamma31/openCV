package fr.rant.opencv.tuto.processing.video;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.bitwise_not;
import static org.bytedeco.opencv.global.opencv_core.inRange;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2HSV;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

public class TresholdInRange {
    private static FrameGrabber frameGrabber;
    private static final OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
    private static JFrame frame;
    private static JLabel imgCaptureLabel;
    private static JLabel imgHsvLabel;
    private static JLabel imgDetectionLabel;
    private static CaptureTask captureTask;
    private static JSlider sliderLowH;
    private static JSlider sliderHighH;
    private static JSlider sliderLowS;
    private static JSlider sliderHighS;
    private static JSlider sliderLowV;
    private static JSlider sliderHighV;

    public static void run() {
        try {
            frameGrabber = FrameGrabber.createDefault(0);
            frameGrabber.setFormat("digrab");
            frameGrabber.setImageWidth(576);
            frameGrabber.setImageHeight(432);
            frameGrabber.start();
            final Mat matFrame = converter.convert(frameGrabber.grab());
            initMainFrame(matFrame);
        } catch (final FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static void initMainFrame(final Mat matFrame) {
        // Create and set up the window.
        frame = new JFrame("Thresholding Operations using inRange demo");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                TresholdInRange.captureTask.cancel(true);
            }
        });
        // Set up the content pane.
        final Image img = Java2DFrameUtils.toBufferedImage(matFrame);

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
        imgHsvLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgHsvLabel);
        imgDetectionLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgDetectionLabel);
        frame.add(framePanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private static class CaptureTask extends SwingWorker<Void, Mat> {
        @Override
        protected Void doInBackground() throws FrameGrabber.Exception {
            while (!isCancelled()) {
                publish(converter.convert(frameGrabber.grab()).clone());
            }
            return null;
        }

        @Override
        protected void process(final List<Mat> frames) {
            final Mat frame = frames.get(frames.size() - 1);
            final Mat frameHSV = new Mat();
            cvtColor(frame, frameHSV, COLOR_BGR2HSV);
            bitwise_not(frameHSV, frameHSV);
            final Mat thresh = new Mat();
            final Mat lowerb = new Mat(new int[]{sliderLowH.getValue(), sliderLowS.getValue(), sliderLowV.getValue()});
            final Mat upperb = new Mat(new int[]{sliderHighH.getValue(), sliderHighS.getValue(), sliderHighV.getValue()});
            inRange(frameHSV, lowerb, upperb, thresh);
            update(frame, frameHSV, thresh);
            
        }

        private static void update(final Mat imgCapture, final Mat imgHsv, final Mat imgThresh) {
            imgCaptureLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(imgCapture)));
            imgHsvLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(imgHsv)));
            imgDetectionLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(imgThresh)));
            frame.repaint();
        }
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(TresholdInRange::run);
    }

}


