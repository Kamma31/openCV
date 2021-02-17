package fr.rant.opencv.tuto.processing.images;

import fr.rant.opencv.TresholdType;
import fr.rant.opencv.Util;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Tresholding {
    private static int thresholdValue = 0;
    private static int thresholdType = 3;
    private static Mat srcGray;
    private static Mat dst;
    private static JFrame frame;
    private static JLabel imgLabel;

    public static void run() {
        final Mat src = Util.getMatResource("butterfly.jpg");
        srcGray = new Mat();
        dst = new Mat();
        // Convert the image to Gray
        cvtColor(src, srcGray, COLOR_BGR2GRAY);

        frame = new JFrame("Threshold");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        addComponentsToPane(frame.getContentPane());

        frame.pack();
        frame.setVisible(true);
    }

    private static void addComponentsToPane(final Container pane) {
        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));

        final JComboBox<String> combo = new JComboBox<>(Arrays.stream(TresholdType.values()).map(TresholdType::getName).toArray(String[]::new));

        sliderPanel.add(new JLabel("Tresholding type"));
        sliderPanel.add(combo);

        final JSlider sliderThreshValue = new JSlider(0, 255, 0);
        sliderThreshValue.setMajorTickSpacing(50);
        sliderThreshValue.setMinorTickSpacing(10);
        sliderThreshValue.setPaintTicks(true);
        sliderThreshValue.setPaintLabels(true);
        sliderPanel.add(sliderThreshValue);

        combo.addActionListener(e -> {
            final JComboBox<String> source = (JComboBox<String>) e.getSource();
            thresholdType = source.getSelectedIndex();
            update();
        });
        sliderThreshValue.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            thresholdValue = source.getValue();
            update();
        });
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(Java2DFrameUtils.toBufferedImage(srcGray)));
        pane.add(imgLabel, BorderLayout.CENTER);
    }

    private static void update() {
        threshold(srcGray, dst, thresholdValue, 255, thresholdType);
        imgLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(dst)));
        frame.repaint();
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();

        SwingUtilities.invokeLater(Tresholding::run);
    }

}
