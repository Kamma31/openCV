package fr.rant.opencv.tuto.processing.images;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.AbstractScalar;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Point3f;
import org.bytedeco.opencv.opencv_imgproc.Vec3fVector;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class CircleDetection {
    private static JFrame frame;
    private static JLabel label;
    private static JSlider dp;
    private static JSlider minDist;
    private static JSlider threshold;
    private static JSlider smoothness;
    private static JSlider minRadius;
    private static JSlider maxRadius;

    public static void run() {
        frame = new JFrame("Hough circle detection");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

        final JPanel sliderPanel = initSliderPanel();
        label = new JLabel();

        frame.add(sliderPanel);
        frame.add(label);
        houghCircle();

        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel initSliderPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(400, 500));
        dp = new JSlider(1, 21, 10);
        dp.setPaintTicks(true);
        dp.setMajorTickSpacing(5);
        dp.setMinorTickSpacing(1);
        minDist = new JSlider(1, 20, 5);
        minDist.setPaintLabels(true);
        minDist.setMajorTickSpacing(5);
        minDist.setMinorTickSpacing(1);
        threshold = new JSlider(1, 200, 100);
        threshold.setPaintLabels(true);
        threshold.setMajorTickSpacing(50);
        threshold.setMinorTickSpacing(25);
        smoothness = new JSlider(1, 300, 100);
        smoothness.setPaintLabels(true);
        smoothness.setMajorTickSpacing(50);
        smoothness.setMinorTickSpacing(25);
        minRadius = new JSlider(1, 50, 1);
        minRadius.setPaintLabels(true);
        minRadius.setMajorTickSpacing(10);
        minRadius.setMinorTickSpacing(5);
        maxRadius = new JSlider(1, 50, 30);
        maxRadius.setPaintLabels(true);
        maxRadius.setMajorTickSpacing(10);
        maxRadius.setMinorTickSpacing(5);

        panel.add(new JLabel("dp * 10"));
        panel.add(dp);
        panel.add(new JLabel("minDist"));
        panel.add(minDist);
        panel.add(new JLabel("threshold"));
        panel.add(threshold);
        panel.add(new JLabel("smoothness"));
        panel.add(smoothness);
        panel.add(new JLabel("minRadius"));
        panel.add(minRadius);
        panel.add(new JLabel("maxRadius"));
        panel.add(maxRadius);

        final ChangeListener changeListener = e -> houghCircle();

        dp.addChangeListener(changeListener);
        minDist.addChangeListener(changeListener);
        threshold.addChangeListener(changeListener);
        smoothness.addChangeListener(changeListener);
        minRadius.addChangeListener(changeListener);
        maxRadius.addChangeListener(changeListener);

        return panel;
    }

    private static void houghCircle() {
        final Mat img = Util.getMatResource("smarties.jpg", IMREAD_COLOR);

        final Mat gray = new Mat();
        cvtColor(img, gray, COLOR_BGR2GRAY);
        medianBlur(gray, gray, 5);
        final Vec3fVector circles = new Vec3fVector();
        HoughCircles(gray, circles, HOUGH_GRADIENT,
                dp.getValue() / 10d, // 	Inverse ratio of the accumulator resolution to the image resolution
                minDist.getValue(), // Min dist beetween two circles change this value to detect circles with different distances to each other
                threshold.getValue(), // Threshold value
                smoothness.getValue(), // Smoothness of circles, the higher the better
                minRadius.getValue(), maxRadius.getValue()); // (min_radius & max_radius) to detect larger circles
        for (int x = 0; x < circles.size(); x++) {
            final Point3f c = circles.get(x);
            final Point center = new Point(Math.round(c.x()), Math.round(c.y()));
            // circle center
            circle(img, center, 1, AbstractScalar.GREEN, 3, 8, 0);
            // circle outline
            final int radius = Math.round(c.z());
            circle(img, center, radius, AbstractScalar.CYAN, 3, 8, 0);
        }

        label.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(img)));
        frame.repaint();
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(CircleDetection::run);
    }
}
