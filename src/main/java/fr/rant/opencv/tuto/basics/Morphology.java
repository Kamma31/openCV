package fr.rant.opencv.tuto.basics;

import fr.rant.opencv.Util;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;

public class Morphology {
    private static final String[] MORPH_OP = {"Opening", "Closing", "Gradient", "Top Hat", "Black Hat"};
    private static final int[] MORPH_OP_TYPE = {Imgproc.MORPH_OPEN, Imgproc.MORPH_CLOSE,
            Imgproc.MORPH_GRADIENT, Imgproc.MORPH_TOPHAT, Imgproc.MORPH_BLACKHAT};
    private static final String[] ELEMENT_TYPE = {"Rectangle", "Cross", "Ellipse"};
    private static final int MAX_KERNEL_SIZE = 21;
    private static Mat matImgSrc;
    private static Mat matImgDst;
    private static int morphOpType = Imgproc.MORPH_OPEN;
    private static int elementType = Imgproc.CV_SHAPE_RECT;
    private static int kernelSize = 0;
    private static JFrame frame;
    private static JLabel imgLabel;

    public static void run() {
        matImgSrc = Util.getMatResource("butterfly.jpg");
        matImgDst = new Mat();

        final Image img = HighGui.toBufferedImage(matImgSrc);
        initMainFrame(img);

        frame.pack();
        frame.setVisible(true);
    }

    private static void initMainFrame(final Image img) {
        frame = new JFrame("Morphology transformations");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));

        final JComboBox<String> morphOpBox = new JComboBox<>(MORPH_OP);
        morphOpBox.addActionListener(e -> {
            final JComboBox<String> cb = (JComboBox<String>) e.getSource();
            morphOpType = MORPH_OP_TYPE[cb.getSelectedIndex()];
            update();
        });

        final JComboBox<String> elementTypeBox = new JComboBox<>(ELEMENT_TYPE);
        elementTypeBox.addActionListener(e -> {
            final JComboBox<String> cb = (JComboBox<String>) e.getSource();
            if (cb.getSelectedIndex() == 0) {
                elementType = Imgproc.CV_SHAPE_RECT;
            } else if (cb.getSelectedIndex() == 1) {
                elementType = Imgproc.CV_SHAPE_CROSS;
            } else if (cb.getSelectedIndex() == 2) {
                elementType = Imgproc.CV_SHAPE_ELLIPSE;
            }
            update();
        });

        final JSlider slider = new JSlider(0, MAX_KERNEL_SIZE, 0);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            kernelSize = source.getValue();
            update();
        });

        sliderPanel.add(morphOpBox);
        sliderPanel.add(elementTypeBox);
        sliderPanel.add(new JLabel("Kernel size: 2n + 1"));
        sliderPanel.add(slider);

        frame.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        frame.add(imgLabel, BorderLayout.CENTER);
    }

    private static void update() {
        final Mat element = Imgproc.getStructuringElement(elementType, new Size(2d * kernelSize + 1, 2d * kernelSize + 1),
                new org.opencv.core.Point(kernelSize, kernelSize));
        Imgproc.morphologyEx(matImgSrc, matImgDst, morphOpType, element);

        imgLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(matImgDst)));
        frame.repaint();
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(Morphology::run);
    }
}
