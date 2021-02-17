package fr.rant.opencv.tuto.basics;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Size;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

import static org.bytedeco.opencv.global.opencv_core.BORDER_DEFAULT;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Smoothing {
    private static final int MAX_KERNEL_LENGTH = 31;

    private static JFrame frame;
    private static JLabel normalizedLabel;
    private static JLabel gaussianLabel;
    private static JLabel medianLabel;
    private static JLabel bilateralLabel;

    public static void run() {
        final Mat src = Util.getMatResource("butterfly.jpg", IMREAD_COLOR);
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
                blur(src, dstBlur, new Size(i, i), new Point(-1, -1), BORDER_DEFAULT);
                normalizedLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(dstBlur)));
                normalizedLabel.repaint();
                time.sleep(sleeptime);
            }

            final Mat dstGaussian = src.clone();
            for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
                GaussianBlur(src, dstGaussian, new Size(i, i), 0, 0, BORDER_DEFAULT);
                gaussianLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(dstGaussian)));
                gaussianLabel.repaint();
                time.sleep(sleeptime);
            }

            final Mat dstMedian = src.clone();
            for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
                medianBlur(src, dstMedian, i);
                medianLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(dstMedian)));
                medianLabel.repaint();
                time.sleep(sleeptime);
            }

            final Mat dstBilateral = src.clone();
            for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
                bilateralFilter(src, dstBilateral, i, i * 2d, i / 2d);
                bilateralLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(dstBilateral)));
                bilateralLabel.repaint();
                time.sleep(sleeptime);
            }
        } catch (final InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private static void initMainFrame(final Mat src) {
        frame = new JFrame("Smoothing transformation PRESS ANY KEY !!!");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        normalizedLabel = Util.newJLabel("Normalized blur");
        normalizedLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(src)));

        gaussianLabel = Util.newJLabel("Gaussian Blur");
        gaussianLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(src)));
        topPanel.add(normalizedLabel);
        topPanel.add(gaussianLabel);

        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        medianLabel = Util.newJLabel("Median Blur");
        medianLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(src)));

        bilateralLabel = Util.newJLabel("Bilateral Blur");
        bilateralLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(src)));
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
