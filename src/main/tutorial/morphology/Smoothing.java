package main.tutorial.morphology;

import main.misc.Util;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.highgui.ImageWindow;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;
import java.util.Map;

public class Smoothing {
    private static final int MAX_KERNEL_LENGTH = 31;
    public static final String PRESS_ENTER = " press key";
    public static final String ORIGINAL_IMAGE = "Original Image" + PRESS_ENTER;
    public static final String NORMALIZED_BLUR = "Normalized blur" + PRESS_ENTER;
    public static final String GAUSSIAN_BLUR = "Gaussian Blur" + PRESS_ENTER;
    public static final String MEDIAN_BLUR = "Median Blur" + PRESS_ENTER;
    public static final String BILATERAL_BLUR = "Bilateral Blur" + PRESS_ENTER;

    static Map<String, ImageWindow> windows = new HashMap<>();

    public static void run() {
        Mat src = Util.getMatResource("dog.jpg", Imgcodecs.IMREAD_COLOR);
        int width = Double.valueOf(src.width() / 1.5d).intValue();
        int height = Double.valueOf(src.height() / 1.5d).intValue();
        displayImg(src, ORIGINAL_IMAGE, width, height, 0);
        resetGuy();
        displayImg(src, NORMALIZED_BLUR, width, height, 1);
        Mat dstBlur = src.clone();
        for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
            Imgproc.blur(src, dstBlur, new Size(i, i), new Point(-1, -1));
            displayDst(dstBlur, NORMALIZED_BLUR);
        }
        resetGuy();

        displayImg(src, GAUSSIAN_BLUR, width, height, 2);
        Mat dstGaussian = src.clone();
        for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
            Imgproc.GaussianBlur(src, dstGaussian, new Size(i, i), 0, 0);
            displayDst(dstGaussian, GAUSSIAN_BLUR);
        }
        resetGuy();

        displayImg(src, MEDIAN_BLUR, width, height, 3);
        Mat dstMedian = src.clone();
        for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
            Imgproc.medianBlur(src, dstMedian, i);
            displayDst(dstMedian, MEDIAN_BLUR);
        }
        resetGuy();

        displayImg(src, BILATERAL_BLUR, width, height, 4);
        Mat dstBilateral = src.clone();
        for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
            Imgproc.bilateralFilter(src, dstBilateral, i, i * 2d, i / 2d);
            displayDst(dstBilateral, BILATERAL_BLUR);
        }
        resetGuy();
        HighGui.windows = windows;

        HighGui.waitKey(1);
    }

    private static void displayImg(Mat src, String imagesNames, int width, int height, int i) {
        HighGui.imshow(imagesNames, src);
        HighGui.resizeWindow(imagesNames, width, height);
        if (!ORIGINAL_IMAGE.equals(imagesNames)) {
            HighGui.moveWindow(imagesNames, 100 * i, 100 * i);
        }
    }

    static void displayDst(Mat dst, String title) {
        HighGui.imshow(title, dst);
    }

    static void resetGuy() {
        windows.putAll(HighGui.windows);
        HighGui.windows = new HashMap<>();
    }


    public static void main(String[] args) {
        Util.loadLibrairies();
        run();
        HighGui.destroyAllWindows();
        System.exit(0);
    }


}
