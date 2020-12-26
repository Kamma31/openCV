package main.tutorial.processing.image;

import main.misc.Tasks;
import main.misc.Util;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class AddingImages {
    private static JFrame frame;
    private static JLabel imgLabel;

    public static void run() {
        // Both images should have same proportions
        Mat src1 = Util.getMatResource("dog.jpg");
        Mat src2 = Util.getMatResource("montagne.jpg");

        frame = new JFrame(Tasks.LINEAR_BLEND.getName());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Image img = HighGui.toBufferedImage(src2);
        addComponentsToPane(frame.getContentPane(), img, src1, src2);

        frame.pack();
        frame.setVisible(true);
    }

    private static void addComponentsToPane(Container pane, Image img, Mat src1, Mat src2) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        AtomicReference<Double> alpha = new AtomicReference<>(0d);
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        final JLabel mainLabel = new JLabel("dst= α/10⋅src1+ (1-α/10)⋅src2", SwingConstants.CENTER);
        mainLabel.setPreferredSize(new Dimension(1000,50));
        sliderPanel.add(mainLabel);
        JSlider slider = new JSlider(0, 10, 0);
        slider.setMajorTickSpacing(1);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            if (source.getValue() != 0) {
                alpha.set(source.getValue() / 10d);
            } else {
                alpha.set(0d);
            }
            update(alpha.get(), src1, src2);
        });
        sliderPanel.add(slider);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(img));
        imgLabel.setBorder(new EmptyBorder(10,10,10,10));
        pane.add(imgLabel, BorderLayout.CENTER);
    }

    static void update(double alpha, Mat src1, Mat src2) {
        Mat dst = new Mat();
        Core.addWeighted(src1, alpha, src2, 1-alpha, 0.0, dst);
        Image img = HighGui.toBufferedImage(dst);
        imgLabel.setIcon(new ImageIcon(img));
        frame.repaint();
    }

    public static void main(String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(AddingImages::run);
    }
}