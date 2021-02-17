package fr.rant.opencv.tuto.basics;

import fr.rant.opencv.Util;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.DataBufferByte;

import static org.bytedeco.opencv.global.opencv_core.*;

public class ContrastBrightness {
    public static final String WINDOWS_NAME = "Contrast and Brightness variations";
    private static JFrame frame;
    private static JSlider alphaSlider;
    private static JSlider betaslider;
    private static JSlider gammaSlider;
    private static JLabel imgResultLabel;
    private static Mat newImage;

    public static void run() {
        final Mat image = Util.getMatResource("rocket.jpg");
        newImage = image.clone();

        frame = new JFrame(WINDOWS_NAME);

        initFrame(frame.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1800, 900));

        final Image img = Java2DFrameUtils.toBufferedImage(image);
        final JPanel framePanel = new JPanel();
        final JLabel imgCaptureLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgCaptureLabel);
        imgResultLabel = new JLabel(new ImageIcon(img));
        framePanel.add(imgResultLabel);
        frame.add(framePanel);

        alphaSlider.addChangeListener(e -> update(image));
        betaslider.addChangeListener(e -> update(image));
        gammaSlider.addChangeListener(e -> update(image));

        alphaSlider.setValue(1);
        betaslider.setValue(0);
        gammaSlider.setValue(1);

        frame.pack();
        frame.setVisible(true);
    }

    private static void initFrame(final Container pane) {
        final JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        final JPanel emptyPanel = new JPanel();
        emptyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        sliderPanel.add(emptyPanel);
        sliderPanel.add(new JLabel("Contrast"));
        alphaSlider = new JSlider(0, 30);
        alphaSlider.setMinorTickSpacing(5);
        alphaSlider.setMajorTickSpacing(10);
        alphaSlider.setPaintTicks(true);
        alphaSlider.setPaintLabels(true);
        sliderPanel.add(alphaSlider);
        sliderPanel.add(new JLabel("Brightness"));
        betaslider = new JSlider(-100, 100);
        betaslider.setMinorTickSpacing(10);
        betaslider.setMajorTickSpacing(50);
        betaslider.setPaintTicks(true);
        betaslider.setPaintLabels(true);
        sliderPanel.add(betaslider);
        sliderPanel.add(new JLabel("Gamma Correction"));
        gammaSlider = new JSlider(0, 30);
        gammaSlider.setMinorTickSpacing(5);
        gammaSlider.setMajorTickSpacing(10);
        gammaSlider.setPaintTicks(true);
        gammaSlider.setPaintLabels(true);
        sliderPanel.add(gammaSlider);

        pane.add(sliderPanel, BorderLayout.PAGE_START);
    }

    private static void update(final Mat image) {
        final byte[] imageData = ((DataBufferByte) Java2DFrameUtils.toBufferedImage(image).getRaster().getDataBuffer()).getData();
        final byte[] tempImageData = new byte[(int) (newImage.total() * newImage.channels())];
        for (int y = 0; y < image.rows(); y++) {
            final int cols = image.cols();
            for (int x = 0; x < cols; x++) {
                final int channels = image.channels();
                for (int c = 0; c < channels; c++) {
                    final int i = (y * cols + x) * channels + c;
                    double pixelValue = imageData[i];
                    pixelValue = pixelValue < 0 ? pixelValue + 256 : pixelValue;
                    tempImageData[i]
                            = saturate(alphaSlider.getValue() * pixelValue + betaslider.getValue());
                }
            }
        }
        final Mat temp = new Mat(newImage.rows(), newImage.cols(), CV_8UC(newImage.channels()), new BytePointer(tempImageData));

        Mat lookUpTable = new Mat(1, 256, CV_8U);
        final byte[] lookUpTableData = new byte[(int) (lookUpTable.total() * lookUpTable.channels())];
        for (int i = 0; i < lookUpTable.cols(); i++) {
            lookUpTableData[i] = saturate(Math.pow(i / 255.0, gammaSlider.getValue()) * 255.0);
        }
        lookUpTable = new Mat(1, 256, CV_8UC(1), new BytePointer(lookUpTableData));
        LUT(temp, lookUpTable, newImage);
        lookUpTable.close();

        imgResultLabel.setIcon(new ImageIcon(Java2DFrameUtils.toBufferedImage(newImage)));
        frame.repaint();
    }

    private static byte saturate(final double val) {
        int iVal = (int) Math.round(val);
        iVal = Math.min(255, Math.max(iVal, 0));
        return (byte) iVal;
    }

    public static void main(final String... args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(ContrastBrightness::run);
    }
}
