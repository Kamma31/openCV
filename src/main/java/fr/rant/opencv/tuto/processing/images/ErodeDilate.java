package fr.rant.opencv.tuto.processing.images;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Size;

import javax.swing.*;
import java.awt.*;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class ErodeDilate {
    private static final String[] ELEMENT_TYPE = {"Rectangle", "Cross", "Ellipse"};
    private static final String[] MORPH_OP = {"Erosion", "Dilatation"};
    private static final int MAX_KERNEL_SIZE = 21;
    private static Mat matImgSrc;
    private static Mat matImgDst;
    private static int elementType = CV_SHAPE_RECT;
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
                elementType = CV_SHAPE_RECT;
            } else if (cb.getSelectedIndex() == 1) {
                elementType = CV_SHAPE_CROSS;
            } else if (cb.getSelectedIndex() == 2) {
                elementType = CV_SHAPE_ELLIPSE;
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
        imgLabel = new JLabel(new ImageIcon(Java2DFrameUtils.toBufferedImage(matImgSrc)));
        frame.add(imgLabel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    private static void update() {
        final Mat element = getStructuringElement(elementType, new Size(2 * kernelSize + 1, 2 * kernelSize + 1),
                new Point(kernelSize, kernelSize));
        if (doErosion) {
            erode(matImgSrc, matImgDst, element);
        } else {
            dilate(matImgSrc, matImgDst, element);
        }
        imgLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(matImgDst)));
        frame.repaint();
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(ErodeDilate::run);
    }

}
