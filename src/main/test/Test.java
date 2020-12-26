package main.test;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class Test {

    public static void run(String... args) {
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println(m.dump());
    }
}