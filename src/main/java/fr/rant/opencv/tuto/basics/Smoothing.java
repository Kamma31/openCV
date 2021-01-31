package fr.rant.opencv.tuto.basics;

import fr.rant.opencv.Util;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

public class Smoothing {
    private static final int MAX_KERNEL_LENGTH = 31;

    private static JFrame frame;
    private static JLabel normalizedLabel;
    private static JLabel gaussianLabel;
    private static JLabel medianLabel;
    private static JLabel bilateralLabel;

    public static void run() {
        final Mat src = Util.getMatResource("butterfly.jpg", Imgcodecs.IMREAD_COLOR);
        initMainFrame(src);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {

            }

            @Override
            public void keyPressed(final KeyEvent e) {
                execute(src);
            }

            @Override
            public void keyReleased(final KeyEvent e) {

            }
        });
    }

    private static void execute(final Mat src) {
        final TimeUnit time = TimeUnit.SECONDS;
        final long sleeptime = (long) 0.5;
        try {
            final Mat dstBlur = src.clone();
            for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
                Imgproc.blur(src, dstBlur, new Size(i, i), new Point(-1, -1));
                normalizedLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(dstBlur)));
                normalizedLabel.repaint();
                time.sleep(sleeptime);
            }

            final Mat dstGaussian = src.clone();
            for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
                Imgproc.GaussianBlur(src, dstGaussian, new Size(i, i), 0, 0);
                gaussianLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(dstGaussian)));
                gaussianLabel.repaint();
                time.sleep(sleeptime);
            }

            final Mat dstMedian = src.clone();
            for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
                Imgproc.medianBlur(src, dstMedian, i);
                medianLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(dstMedian)));
                medianLabel.repaint();
                time.sleep(sleeptime);
            }

            final Mat dstBilateral = src.clone();
            for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
                Imgproc.bilateralFilter(src, dstBilateral, i, i * 2d, i / 2d);
                bilateralLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(dstBilateral)));
                bilateralLabel.repaint();
                time.sleep(sleeptime);
            }
        } catch (final InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private static void initMainFrame(final Mat src) {
        frame = new JFrame("Smoothing transformation");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        normalizedLabel = Util.newJLabel("Normalized blur");
        normalizedLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(src)));

        gaussianLabel = Util.newJLabel("Gaussian Blur");
        gaussianLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(src)));
        topPanel.add(normalizedLabel);
        topPanel.add(gaussianLabel);

        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        medianLabel = Util.newJLabel("Median Blur");
        medianLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(src)));

        bilateralLabel = Util.newJLabel("Bilateral Blur");
        bilateralLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(src)));
        bottomPanel.add(medianLabel);
        bottomPanel.add(bilateralLabel);

        frame.add(topPanel);
        frame.add(bottomPanel);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(Smoothing::run);
    }


}
