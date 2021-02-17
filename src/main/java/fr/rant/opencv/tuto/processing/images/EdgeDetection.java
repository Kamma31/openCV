package fr.rant.opencv.tuto.processing.images;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import javax.swing.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class EdgeDetection {
    private static final String[] OPERATION = {"Sobel", "Scharr", "Laplace", "Canny"};
    private static final int MAX_LOW_THRESHOLD = 100;
    private static final int KERNEL_SIZE = 3;
    private static final double RATIO = 3;
    private static final int SCALE = 1;
    private static final int DELTA = 0;
    private static final int DDEPTH = CV_16S;
    private static final Size BLUR_SIZE = new Size(3, 3);
    private static JFrame frame;
    private static Mat src;
    private static JLabel imgResLabel;

    public static void run() {
        src = Util.getMatResource("butterfly.jpg");
        frame = new JFrame("Edge detection");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

        final JComboBox<String> operationBox = new JComboBox<>(OPERATION);
        frame.add(operationBox);

        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("Min Threshold:"));
        final JSlider slider = new JSlider(0, MAX_LOW_THRESHOLD, 0);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        sliderPanel.add(slider);
        slider.setEnabled(false);
        slider.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            cannyEdgeDetector(source.getValue());
        });

        frame.add(sliderPanel);

        final JPanel framePanel = new JPanel();
        final JLabel imgSrcLabel = new JLabel(new ImageIcon(Java2DFrameUtils.toBufferedImage(src)));
        framePanel.add(imgSrcLabel);
        imgResLabel = new JLabel(new ImageIcon(Java2DFrameUtils.toBufferedImage(src)));
        framePanel.add(imgResLabel);

        operationBox.addActionListener(e -> {
            slider.setEnabled(false);
            final JComboBox<String> source = (JComboBox<String>) e.getSource();
            switch (source.getSelectedIndex()) {
                case 0:
                    sobelOrScharr(OPERATION[0]);
                    break;
                case 1:
                    sobelOrScharr(OPERATION[1]);
                    break;
                case 2:
                    laplace();
                    break;
                case 3:
                    slider.setEnabled(true);
                    slider.setValue(0);
                    break;
                default:
                    break;
            }
        });
        operationBox.setSelectedIndex(0);

        frame.add(framePanel);
        frame.pack();
        frame.setVisible(true);
    }

    private static void sobelOrScharr(final String operation) {
        final Mat srcGray = new Mat();
        final Mat grad = new Mat();
        final Mat dst = new Mat();

        // Remove noise by blurring with a Gaussian filter ( kernel size = 3 )
        GaussianBlur(src, dst, new Size(3, 3), 0, 0, BORDER_DEFAULT);
        // Convert the image to grayscale
        cvtColor(dst, srcGray, COLOR_RGB2GRAY);
        final Mat gradX = new Mat();
        final Mat gradY = new Mat();
        final Mat absGradX = new Mat();
        final Mat absGradY = new Mat();
        if (OPERATION[0].equals(operation)) {
            Sobel(srcGray, gradX, DDEPTH, 1, 0, 3, SCALE, DELTA, BORDER_DEFAULT);
            Sobel(srcGray, gradY, DDEPTH, 0, 1, 3, SCALE, DELTA, BORDER_DEFAULT);
        }
        if (OPERATION[1].equals(operation)) {
            Scharr(srcGray, gradX, DDEPTH, 1, 0, SCALE, DELTA, BORDER_DEFAULT);
            Scharr(srcGray, gradY, DDEPTH, 0, 1, SCALE, DELTA, BORDER_DEFAULT);
        }
        // converting back to CV_8U
        convertScaleAbs(gradX, absGradX);
        convertScaleAbs(gradY, absGradY);
        addWeighted(absGradX, 0.5, absGradY, 0.5, 0, grad);

        imgResLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(grad)));
        frame.repaint();
    }

    public static void laplace() {
        final Mat srcGray = new Mat();
        final Mat blurr = new Mat();
        final Mat dst = new Mat();

        final int kernelSize = 3;

        GaussianBlur(src, blurr, new Size(3, 3), 0, 0, BORDER_DEFAULT);
        cvtColor(blurr, srcGray, COLOR_RGB2GRAY);
        final Mat absDst = new Mat();
        Laplacian(srcGray, dst, DDEPTH, kernelSize, SCALE, DELTA, BORDER_DEFAULT);
        convertScaleAbs(dst, absDst);

        imgResLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(absDst)));
        frame.repaint();
    }

    private static void cannyEdgeDetector(final int lowThresh) {
        final Mat srcBlur = new Mat();
        final Mat detectedEdges = new Mat();
        blur(src, srcBlur, BLUR_SIZE);
        Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
        final Mat dst = new Mat(src.size(), CV_8UC3, Scalar.all(0));
        src.copyTo(dst, detectedEdges);

        imgResLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(dst)));
        frame.repaint();
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(EdgeDetection::run);
    }
}
