package main.tutorial.processing.image;

import main.misc.Util;
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
    private final Mat matImgSrc;
    private final Mat matImgDst = new Mat();
    private int elementType = Imgproc.CV_SHAPE_RECT;
    private int kernelSize = 0;
    private boolean doErosion = true;
    private final JFrame frame;
    private JLabel imgLabel;

    public ErodeDilate(String title) {
        matImgSrc = Util.getMatResource(title);

        frame = new JFrame("Erosion and dilatation demo");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Image img = HighGui.toBufferedImage(matImgSrc);
        addComponentsToPane(frame.getContentPane(), img);

        frame.pack();
        frame.setVisible(true);
    }

    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
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
        JComboBox<String> morphOpBox = new JComboBox<>(MORPH_OP);
        morphOpBox.addActionListener(e -> {
            @SuppressWarnings("unchecked")
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            doErosion = cb.getSelectedIndex() == 0;
            update();
        });
        sliderPanel.add(morphOpBox);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }

    private void update() {
        Mat element = Imgproc.getStructuringElement(elementType, new Size(2d * kernelSize + 1, 2d * kernelSize + 1),
                new org.opencv.core.Point(kernelSize, kernelSize));
        if (doErosion) {
            Imgproc.erode(matImgSrc, matImgDst, element);
        } else {
            Imgproc.dilate(matImgSrc, matImgDst, element);
        }
        Image img = HighGui.toBufferedImage(matImgDst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

    public static void main(String[] args) {
        Util.loadLibrairies();
        run("LinuxLogo.jpg");
    }

    public static void run(String title) {
        SwingUtilities.invokeLater(() -> new ErodeDilate(title));
    }
}
