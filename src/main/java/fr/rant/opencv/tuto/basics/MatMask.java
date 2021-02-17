package fr.rant.opencv.tuto.basics;

import fr.rant.opencv.Util;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;


public class MatMask {
    private static JFrame frame;
    private static JLabel handLabel;
    private static JLabel builtInLabel;

    public static void run() {
        final Mat src = Util.getMatResourceOld("lena.png", Imgcodecs.IMREAD_COLOR);
        Imgproc.resize(src, src, new Size(375, 375));
        initFrame(src);

        handWritten(src);
        builtIn(src);

        frame.pack();
        frame.repaint();
    }

    private static void builtIn(final Mat src) {
        final Mat kern = new Mat(3, 3, CvType.CV_8S);
        final int row = 0;
        final int col = 0;
        kern.put(row, col, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        double t = System.currentTimeMillis();
        final Mat dst1 = new Mat();
        Imgproc.filter2D(src, dst1, src.depth(), kern);
        t = ((double) System.currentTimeMillis() - t) / 1000;
        final String out2Name = "Built-in filter2D : " + t + "s";
        builtInLabel.setText(out2Name);
        builtInLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(dst1)));
    }

    private static void handWritten(final Mat src) {
        double t = System.currentTimeMillis();
        final Mat handLabelMat = sharpen(src);
        t = ((double) System.currentTimeMillis() - t) / 1000;
        final String handLabelName = "Hand written : " + t + "s";
        handLabel.setText(handLabelName);
        handLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(handLabelMat)));
    }

    private static void initFrame(final Mat src) {
        frame = new JFrame("Mat mask time comparison");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        final JLabel srcLabel = Util.newJLabel("Source image");
        srcLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(src)));

        frame.add(srcLabel);

        final JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.X_AXIS));
        handLabel = Util.newJLabel();
        builtInLabel = Util.newJLabel();

        resultPanel.add(handLabel);
        resultPanel.add(builtInLabel);

        frame.add(resultPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private static double saturate(final double x) {
        return x > 255.0 ? 255.0 : (Math.max(x, 0.0));
    }

    private static Mat sharpen(final Mat myImage) {
        final Mat result = new Mat();
        myImage.convertTo(myImage, CvType.CV_8U);
        final int nChannels = myImage.channels();
        result.create(myImage.size(), myImage.type());
        for (int j = 1; j < myImage.rows() - 1; ++j) {
            for (int i = 1; i < myImage.cols() - 1; ++i) {
                final double[] sum = new double[nChannels];
                for (int k = 0; k < nChannels; ++k) {
                    final double top = -myImage.get(j - 1, i)[k];
                    final double bottom = -myImage.get(j + 1, i)[k];
                    final double center = (5 * myImage.get(j, i)[k]);
                    final double left = -myImage.get(j, i - 1)[k];
                    final double right = -myImage.get(j, i + 1)[k];
                    sum[k] = saturate(top + bottom + center + left + right);
                }
                result.put(j, i, sum);
            }
        }
        result.row(0).setTo(Scalar.all(0));
        result.row(result.rows() - 1).setTo(Scalar.all(0));
        result.col(0).setTo(Scalar.all(0));
        result.col(result.cols() - 1).setTo(Scalar.all(0));
        return result;
    }

    public static void main(final String... args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(MatMask::run);
    }
}
