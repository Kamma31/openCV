package fr.rant.opencv.tuto.processing.images;

import fr.rant.opencv.Util;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;

public class ErodeDilate {
    private static final String[] ELEMENT_TYPE = {"Rectangle", "Cross", "Ellipse"};
    private static final String[] MORPH_OP = {"Erosion", "Dilatation"};
    private static final int MAX_KERNEL_SIZE = 21;
    private static Mat matImgSrc;
    private static Mat matImgDst;
    private static int elementType = Imgproc.CV_SHAPE_RECT;
    private static int kernelSize = 0;
    private static boolean doErosion = true;
    private static JFrame frame;
    private static JLabel imgLabel;

    public static void run() {
        matImgSrc = Util.getMatResource("lena.png");
        matImgDst = new Mat();

        initMainPane();

    }

    private static void initMainPane() {
        frame = new JFrame("Erosion and dilatation demo");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
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
        sliderPanel.add(elementTypeBox);
        sliderPanel.add(new JLabel("Kernel size: 2n + 1"));
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
        sliderPanel.add(slider);
        final JComboBox<String> morphOpBox = new JComboBox<>(MORPH_OP);
        morphOpBox.addActionListener(e -> {
            final JComboBox<String> cb = (JComboBox<String>) e.getSource();
            doErosion = cb.getSelectedIndex() == 0;
            update();
        });
        sliderPanel.add(morphOpBox);
        frame.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(matImgSrc)));
        frame.add(imgLabel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private static void update() {
        final Mat element = Imgproc.getStructuringElement(elementType, new Size(2d * kernelSize + 1, 2d * kernelSize + 1),
                new org.opencv.core.Point(kernelSize, kernelSize));
        if (doErosion) {
            Imgproc.erode(matImgSrc, matImgDst, element);
        } else {
            Imgproc.dilate(matImgSrc, matImgDst, element);
        }
        final Image img = HighGui.toBufferedImage(matImgDst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(ErodeDilate::run);
    }

}
