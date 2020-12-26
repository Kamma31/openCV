package main.tutorial.processing.video;

import main.misc.Util;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class EdgeDetection {
    private static final String WINDOW_NAME = "Edge Detection";
    private static final String[] OPERATION = {"Sobel", "Scharr", "Laplace", "Canny"};
    private static final int MAX_LOW_THRESHOLD = 100;
    private static final int KERNEL_SIZE = 3;
    private static final double RATIO = 3;
    private static final int SCALE = 1;
    private static final int DELTA = 0;
    private static final int DDEPTH = CvType.CV_16S;
    private static final Size BLUR_SIZE = new Size(3, 3);
    private static JFrame frame;
    private static Mat src;
    private static JLabel imgCaptureLabel;
    private static JLabel imgResLabel;
    private static VideoCapture cap;
    private static CaptureTask captureTask;
    private static JComboBox<String> operationBox;
    private static JSlider slider;

    public static void run() {
        src = new Mat();
        cap = new VideoCapture(0);
        cap.read(src);

        frame = new JFrame(WINDOW_NAME);
        initFrame(frame.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                captureTask.cancel(true);
            }
        });

        Image imgSrc = HighGui.toBufferedImage(src);
        JPanel framePanel = new JPanel();
        imgCaptureLabel = new JLabel(new ImageIcon(imgSrc));
        framePanel.add(imgCaptureLabel);
        imgResLabel = new JLabel(new ImageIcon(imgSrc));
        framePanel.add(imgResLabel);
        frame.add(framePanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static void initFrame(Container contentPane) {
        JPanel controlPanel = new JPanel();
        operationBox = new JComboBox<>(OPERATION);
        operationBox.setSelectedIndex(0);
        controlPanel.add(operationBox);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("Min Threshold:"));
        slider = new JSlider(0, MAX_LOW_THRESHOLD, 0);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setEnabled(false);
        slider.setValue(0);

        sliderPanel.add(slider);

        controlPanel.add(sliderPanel);

        contentPane.add(controlPanel, BorderLayout.PAGE_START);
    }

    private static class CaptureTask extends SwingWorker<Void, Mat> {
        private Mat thresh;

        @Override
        protected Void doInBackground() {
            Mat matFrame = new Mat();
            while (!isCancelled()) {
                if (!cap.read(matFrame)) {
                    break;
                }
                publish(matFrame.clone());
            }
            return null;
        }

        @Override
        protected void process(List<Mat> frames) {
            src = frames.get(frames.size() - 1);
            switch (operationBox.getSelectedIndex()) {
                case 0:
                    slider.setEnabled(false);
                    thresh = sobelOrScharr(OPERATION[0]);
                    break;
                case 1:
                    slider.setEnabled(false);
                    thresh = sobelOrScharr(OPERATION[1]);
                    break;
                case 2:
                    slider.setEnabled(false);
                    thresh = laplace();
                    break;
                case 3:
                    slider.setEnabled(true);
                    thresh = cannyEdgeDetector(slider.getValue());
                    break;
                default:
                    break;
            }
            update(thresh);
        }

        private static void update(Mat imgThresh) {
            imgCaptureLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(src)));
            imgResLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(imgThresh)));
            frame.repaint();
        }

        private static Mat sobelOrScharr(String operation) {
            Mat srcGray = new Mat();
            Mat grad = new Mat();
            Mat dst = new Mat();

            // Remove noise by blurring with a Gaussian filter ( kernel size = 3 )
            Imgproc.GaussianBlur(src, dst, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
            // Convert the image to grayscale
            Imgproc.cvtColor(dst, srcGray, Imgproc.COLOR_RGB2GRAY);
            Mat gradX = new Mat();
            Mat gradY = new Mat();
            Mat absGradX = new Mat();
            Mat absGradY = new Mat();
            if (OPERATION[0].equals(operation)) {
                Imgproc.Sobel(srcGray, gradX, DDEPTH, 1, 0, 3, SCALE, DELTA, Core.BORDER_DEFAULT);
                Imgproc.Sobel(srcGray, gradY, DDEPTH, 0, 1, 3, SCALE, DELTA, Core.BORDER_DEFAULT);
            }
            if (OPERATION[1].equals(operation)) {
                Imgproc.Scharr(srcGray, gradX, DDEPTH, 1, 0, SCALE, DELTA, Core.BORDER_DEFAULT);
                Imgproc.Scharr(srcGray, gradY, DDEPTH, 0, 1, SCALE, DELTA, Core.BORDER_DEFAULT);
            }
            // converting back to CV_8U
            Core.convertScaleAbs(gradX, absGradX);
            Core.convertScaleAbs(gradY, absGradY);
            Core.addWeighted(absGradX, 0.5, absGradY, 0.5, 0, grad);

            return grad;
        }

        public static Mat laplace() {
            Mat srcGray = new Mat();
            Mat blurr = new Mat();
            Mat dst = new Mat();

            int kernelSize = 3;

            Imgproc.GaussianBlur(src, blurr, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
            Imgproc.cvtColor(blurr, srcGray, Imgproc.COLOR_RGB2GRAY);
            Mat absDst = new Mat();
            Imgproc.Laplacian(srcGray, dst, DDEPTH, kernelSize, SCALE, DELTA, Core.BORDER_DEFAULT);
            Core.convertScaleAbs(dst, absDst);

            return absDst;
        }

        private static Mat cannyEdgeDetector(int lowThresh) {
            Mat srcBlur = new Mat();
            Mat detectedEdges = new Mat();
            Imgproc.blur(src, srcBlur, BLUR_SIZE);
            Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
            Mat dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
            src.copyTo(dst, detectedEdges);

            return dst;
        }
    }

    public static void main(String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(EdgeDetection::run);
    }
}
