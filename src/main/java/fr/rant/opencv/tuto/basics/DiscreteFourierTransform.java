package fr.rant.opencv.tuto.basics;

import fr.rant.opencv.Util;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.*;

import javax.swing.*;
import java.awt.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

public class DiscreteFourierTransform {
    private static JFrame frame;
    private static JLabel droitLabel;
    private static JLabel inclineLabel;

    public static void run() {
        final Mat matDroit = Util.getMatResource("hand_writed.jpg", IMREAD_GRAYSCALE);
        final Mat matIncl = Util.getMatResource("hand_writed_incl.jpg", IMREAD_GRAYSCALE);
        resize(matDroit, matDroit, new Size(530, 530));
        resize(matIncl, matIncl, new Size(530, 530));

        initMainFrame(matDroit, matIncl);

        doDFT(matDroit, droitLabel);
        doDFT(matIncl, inclineLabel);

        frame.pack();
        frame.repaint();
    }

    private static void initMainFrame(final Mat matDroit, final Mat matIncl) {
        frame = new JFrame("Discrete Fourier transform");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        final JLabel srcLeftLabel = new JLabel();
        srcLeftLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(matDroit)));
        droitLabel = new JLabel();
        leftPanel.add(srcLeftLabel);
        leftPanel.add(droitLabel);

        final JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        final JLabel srcRightLabel = new JLabel();
        srcRightLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(matIncl)));
        inclineLabel = new JLabel();
        rightPanel.add(srcRightLabel);
        rightPanel.add(inclineLabel);

        frame.add(leftPanel);
        frame.add(Box.createRigidArea(new Dimension(20, 0)));
        frame.add(rightPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private static void doDFT(final Mat mat, final JLabel label) {
        final Mat padded = new Mat();
        //expand input image to optimal size
        final int m = getOptimalDFTSize(mat.rows());
        final int n = getOptimalDFTSize(mat.cols());
        // on the border add zero values
        copyMakeBorder(mat, padded, 0, m - mat.rows(), 0, n - mat.cols(), BORDER_CONSTANT, Scalar.all(0));
        padded.convertTo(padded, CV_32F);
        final MatVector planes = new MatVector(padded, Mat.zeros(padded.size(), CV_32F).asMat());
        final Mat complexI = new Mat();
        merge(planes, complexI);         // Add to the expanded another plane with zeros
        dft(complexI, complexI);         // this way the result may fit in the source matrix
        // compute the magnitude and switch to logarithmic scale
        // => log(1 + sqrt(Re(DFT(mat))^2 + Im(DFT(mat))^2))
        split(complexI, planes);                               // planes.get(0) = Re(DFT(mat)
        // planes.get(1) = Im(DFT(mat))
        magnitude(planes.get(0), planes.get(1), planes.get(0));// planes.get(0) = magnitude
        Mat magI = planes.get(0);
        final Mat matOfOnes = Mat.ones(magI.size(), magI.type()).asMat();
        add(matOfOnes, magI, magI);         // switch to logarithmic scale
        log(magI, magI);
        // crop the spectrum, if it has an odd number of rows or columns
        magI = magI.apply(new Rect(0, 0, magI.cols() & -2, magI.rows() & -2));
        // rearrange the quadrants of Fourier image  so that the origin is at the image center
        final int cx = magI.cols() / 2;
        final int cy = magI.rows() / 2;
        final Mat q0 = magI.apply(new Rect(0, 0, cx, cy));   // Top-Left - Create a ROI per quadrant
        final Mat q1 = magI.apply(new Rect(cx, 0, cx, cy));  // Top-Right
        final Mat q2 = magI.apply(new Rect(0, cy, cx, cy));  // Bottom-Left
        final Mat q3 = magI.apply(new Rect(cx, cy, cx, cy)); // Bottom-Right
        // swap quadrants (Top-Left with Bottom-Right)
        final Mat tmp = new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
        q2.copyTo(q1);
        tmp.copyTo(q2);
        magI.convertTo(magI, CV_8UC1);
        // Transform the matrix with float values
        // into a viewable image form (float between values 0 and 255).
        normalize(magI, magI, 0d, 255d, NORM_MINMAX, CV_8UC1, null);

        label.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(magI)));
    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(DiscreteFourierTransform::run);
    }
}