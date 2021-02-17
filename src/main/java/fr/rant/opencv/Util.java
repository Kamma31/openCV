package fr.rant.opencv;

import com.bulenkov.darcula.DarculaLaf;
import nu.pattern.OpenCV;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public class Util {
    public static Mat getMatResource(final String filename) {
        return imread(getResource(filename));
    }

    public static org.opencv.core.Mat getMatResourceOld(final String filename) {
        return Imgcodecs.imread(getResource(filename));
    }


    public static Mat getMatResource(final String filename, final int imgcodecs) {
        return imread(getResource(filename), imgcodecs);
    }

    public static org.opencv.core.Mat getMatResourceOld(final String filename, final int imgcodecs) {
        return Imgcodecs.imread(getResource(filename), imgcodecs);
    }

    public static String getResource(final String filename) {
        return Util.class.getResource("/" + filename).getPath().substring(1);
    }

    public static void loadLibrairies() {
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
        } catch (final UnsupportedLookAndFeelException ignored) {
        }
        OpenCV.loadLocally();
    }

    public static JLabel newJLabel(final String text) {
        final JLabel label = new JLabel(text);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setVerticalTextPosition(SwingConstants.TOP);
        label.setFont(label.getFont().deriveFont(14f));
        return label;
    }

    public static JLabel newJLabel() {
        return newJLabel("");
    }

    public static Mat listPointsToMat(final List<Point2f> point2fList) {
        final Mat mat = Mat.zeros(point2fList.size() * 2, 1, CV_8UC1).asMat();
        try (final UByteRawIndexer idx = mat.createIndexer()) {
            for (int i = 0; i < point2fList.size(); i++) {
                final Point2f p = point2fList.get(i);
                idx.putDouble(new long[]{1, i * 2 - 1}, p.x());
                idx.putDouble(new long[]{1, i * 2}, p.y());
            }
        }
        return mat;
    }
}
