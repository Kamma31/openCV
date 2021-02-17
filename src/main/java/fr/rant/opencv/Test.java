package fr.rant.opencv;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.print;

public class Test {

    public static void main(final String... args) {
        final Mat m = new Mat(5, 10, CV_8UC1, new Scalar(0));
        final Mat mr1 = m.row(1);
        mr1.setTo(new Mat(new Scalar(1)));
        final Mat mc5 = m.col(5);
        mc5.setTo(new Mat(new Scalar(5)));
        print(m);
    }
}
