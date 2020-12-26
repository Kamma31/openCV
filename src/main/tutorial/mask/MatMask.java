package main.tutorial.mask;

import com.bulenkov.darcula.DarculaLaf;
import main.misc.Util;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;

public class MatMask {

    public static void run() {
        Mat src = Util.getMatResource("lena.png", Imgcodecs.IMREAD_COLOR);
        HighGui.namedWindow("Input", HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow("Input", src);
        double t = System.currentTimeMillis();
        Mat dst0 = sharpen(src, new Mat());
        t = ((double) System.currentTimeMillis() - t) / 1000;
        final String out1Name = "Hand written : " + t + "s";
        HighGui.namedWindow(out1Name, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(out1Name, dst0);
        HighGui.moveWindow(out1Name, 200, 200);

        Mat kern = new Mat(3, 3, CvType.CV_8S);
        int row = 0;
        int col = 0;
        kern.put(row, col, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        t = System.currentTimeMillis();
        Mat dst1 = new Mat();
        Imgproc.filter2D(src, dst1, src.depth(), kern);
        t = ((double) System.currentTimeMillis() - t) / 1000;
        final String out2Name = "Built-in filter2D : " + t + "s";
        HighGui.namedWindow(out2Name, HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow(out2Name, dst1);
        HighGui.moveWindow(out2Name, 400, 400);
        HighGui.waitKey(1);
    }

    public static double saturate(double x) {
        return x > 255.0 ? 255.0 : (Math.max(x, 0.0));
    }

    public static Mat sharpen(Mat myImage, Mat result) {
        myImage.convertTo(myImage, CvType.CV_8U);
        int nChannels = myImage.channels();
        result.create(myImage.size(), myImage.type());
        for (int j = 1; j < myImage.rows() - 1; ++j) {
            for (int i = 1; i < myImage.cols() - 1; ++i) {
                double[] sum = new double[nChannels];
                for (int k = 0; k < nChannels; ++k) {
                    double top = -myImage.get(j - 1, i)[k];
                    double bottom = -myImage.get(j + 1, i)[k];
                    double center = (5 * myImage.get(j, i)[k]);
                    double left = -myImage.get(j, i - 1)[k];
                    double right = -myImage.get(j, i + 1)[k];
                    sum[k] = saturate(top + bottom + center + left + right);
                }
                result.put(j, i, sum);
            }
        }
        result.row(0).setTo(new Scalar(0));
        result.row(result.rows() - 1).setTo(new Scalar(0));
        result.col(0).setTo(new Scalar(0));
        result.col(result.cols() - 1).setTo(new Scalar(0));
        return result;
    }

    public static void main(String... args) {
        Util.loadLibrairies();
        run();
        System.exit(0);
    }
}
