package main.tutorial.theorics;

import main.misc.Util;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

public class DiscreteFourierTransform {
    private static void help() {
        System.out.println("""
                This program demonstrated the use of the discrete Fourier transform (DFT).
                The dft of an image is taken and it's power spectrum is displayed.
                """);
    }

    public static void run() {
        help();
        Mat mat_droit = Util.getMatResource("hand_writed.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        Mat mat_incl = Util.getMatResource("hand_writed_incl.jpg", Imgcodecs.IMREAD_GRAYSCALE);

        doDFT(mat_droit, "normal");
        doDFT(mat_incl, "incl");
        HighGui.waitKey(1);
    }

    private static void doDFT(Mat mat, String type) {
        Mat padded = new Mat();
        //expand input image to optimal size
        int m = Core.getOptimalDFTSize(mat.rows());
        int n = Core.getOptimalDFTSize(mat.cols());
        // on the border add zero values
        Core.copyMakeBorder(mat, padded, 0, m - mat.rows(), 0, n - mat.cols(), Core.BORDER_CONSTANT, Scalar.all(0));
        List<Mat> planes = new ArrayList<>();
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
        Mat complexI = new Mat();
        Core.merge(planes, complexI);         // Add to the expanded another plane with zeros
        Core.dft(complexI, complexI);         // this way the result may fit in the source matrix
        // compute the magnitude and switch to logarithmic scale
        // => log(1 + sqrt(Re(DFT(mat))^2 + Im(DFT(mat))^2))
        Core.split(complexI, planes);                               // planes.get(0) = Re(DFT(mat)
        // planes.get(1) = Im(DFT(mat))
        Core.magnitude(planes.get(0), planes.get(1), planes.get(0));// planes.get(0) = magnitude
        Mat magI = planes.get(0);
        Mat matOfOnes = Mat.ones(magI.size(), magI.type());
        Core.add(matOfOnes, magI, magI);         // switch to logarithmic scale
        Core.log(magI, magI);
        // crop the spectrum, if it has an odd number of rows or columns
        magI = magI.submat(new Rect(0, 0, magI.cols() & -2, magI.rows() & -2));
        // rearrange the quadrants of Fourier image  so that the origin is at the image center
        int cx = magI.cols() / 2;
        int cy = magI.rows() / 2;
        Mat q0 = new Mat(magI, new Rect(0, 0, cx, cy));   // Top-Left - Create a ROI per quadrant
        Mat q1 = new Mat(magI, new Rect(cx, 0, cx, cy));  // Top-Right
        Mat q2 = new Mat(magI, new Rect(0, cy, cx, cy));  // Bottom-Left
        Mat q3 = new Mat(magI, new Rect(cx, cy, cx, cy)); // Bottom-Right
        Mat tmp = new Mat();               // swap quadrants (Top-Left with Bottom-Right)
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
        q2.copyTo(q1);
        tmp.copyTo(q2);
        magI.convertTo(magI, CvType.CV_8UC1);
        Core.normalize(magI, magI, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1); // Transform the matrix with float values
        // into a viewable image form (float between
        // values 0 and 255).
        HighGui.imshow("Input Image " + type, mat);    // Show the result
        HighGui.imshow("Spectrum Magnitude " + type, magI);
    }

    public static void main(String[] args) {
        Util.loadLibrairies();
        run();
    }
}