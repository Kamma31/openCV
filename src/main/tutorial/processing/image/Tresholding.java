package main.tutorial.processing.image;

import main.misc.TresholdType;
import main.misc.Util;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Tresholding {
    private static final String WINDOW_NAME = "Threshold";
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
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);

        frame = new JFrame(WINDOW_NAME);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final Image img = HighGui.toBufferedImage(srcGray);
        addComponentsToPane(frame.getContentPane(), img);

        frame.pack();
        frame.setVisible(true);
    }

    private static void addComponentsToPane(final Container pane, final Image img) {
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
        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }

    private static void update() {
        Imgproc.threshold(srcGray, dst, thresholdValue, 255, thresholdType);
        final Image img = HighGui.toBufferedImage(dst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();

        javax.swing.SwingUtilities.invokeLater(Tresholding::run);
    }

}
