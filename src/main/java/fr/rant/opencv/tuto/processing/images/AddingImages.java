package fr.rant.opencv.tuto.processing.images;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.bytedeco.opencv.global.opencv_core.addWeighted;

public class AddingImages {
    private static JFrame frame;
    private static JLabel imgLabel;

    public static void run() {
        initMainFrame();

        frame.pack();
        frame.setVisible(true);
    }

    private static void initMainFrame() {
        // Both images should have the same size
        final Mat src1 = Util.getMatResource("dog.jpg");
        final Mat src2 = Util.getMatResource("montagne.jpg");

        frame = new JFrame("Images blending");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final AtomicReference<Double> alpha = new AtomicReference<>(0d);
        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        final JSlider slider = new JSlider(0, 10, 0);
        slider.setMajorTickSpacing(1);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            if (source.getValue() != 0) {
                alpha.set(source.getValue() / 10d);
            } else {
                alpha.set(0d);
            }
            update(alpha.get(), src1, src2);
        });
        sliderPanel.add(slider);
        frame.add(sliderPanel, BorderLayout.PAGE_START);
        imgLabel = new JLabel(new ImageIcon(Java2DFrameUtils.toBufferedImage(src2)));
        imgLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.add(imgLabel, BorderLayout.CENTER);
    }

    static void update(final double alpha, final Mat src1, final Mat src2) {
        final Mat dst = new Mat();
        addWeighted(src1, alpha, src2, 1 - alpha, 0.0, dst);

        imgLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(dst)));
        frame.repaint();
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(AddingImages::run);
    }
}