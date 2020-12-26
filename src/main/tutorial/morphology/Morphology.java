package main.tutorial.morphology;

import main.misc.Util;
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
    private static Mat matImgDst ;
    private static int morphOpType = Imgproc.MORPH_OPEN;
    private static int elementType = Imgproc.CV_SHAPE_RECT;
    private static int kernelSize = 0;
    private static JFrame frame;
    private static JLabel imgLabel;

    public static void run() {
        matImgSrc = Util.getMatResource("butterfly.jpg");
        matImgDst = new Mat();

        frame = new JFrame("Morphology Transformations demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Image img = HighGui.toBufferedImage(matImgSrc);
        addComponentsToPane(frame.getContentPane(), img);

        frame.pack();
        frame.setVisible(true);
    }

    private static void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        JComboBox<String> morphOpBox = new JComboBox<>(MORPH_OP);
        morphOpBox.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            morphOpType = MORPH_OP_TYPE[cb.getSelectedIndex()];
            update();
        });
        sliderPanel.add(morphOpBox);
        JComboBox<String> elementTypeBox = new JComboBox<>(ELEMENT_TYPE);
        elementTypeBox.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
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
        JSlider slider = new JSlider(0, MAX_KERNEL_SIZE, 0);
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            kernelSize = source.getValue();
            update();
        });
        sliderPanel.add(slider);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }

    private static void update() {
        Mat element = Imgproc.getStructuringElement(elementType, new Size(2d * kernelSize + 1, 2d * kernelSize + 1),
                new org.opencv.core.Point(kernelSize, kernelSize));
        Imgproc.morphologyEx(matImgSrc, matImgDst, morphOpType, element);
        Image img = HighGui.toBufferedImage(matImgDst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

    public static void main(String[] args) {
        Util.loadLibrairies();
        javax.swing.SwingUtilities.invokeLater(Morphology::run);
    }
}
