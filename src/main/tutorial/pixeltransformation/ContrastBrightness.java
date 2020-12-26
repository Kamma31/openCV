package main.tutorial.pixeltransformation;

import main.misc.Util;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ContrastBrightness {
    public static final String WINDOWS_NAME = "Contrast and Brightness variations";
    private static JFrame frame;
    private static JSlider alphaSlider;
    private static JSlider betaslider;
    private static JSlider gammaSlider;
    private static JLabel imgResultLabel;
    private static Mat newImage;

    public static void run() {
        Mat image = Util.getMatResource("rocket.jpg");
        newImage = image.clone();

        frame = new JFrame(WINDOWS_NAME);

        initFrame(frame.getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Image img = HighGui.toBufferedImage(image);
        JPanel framePanel = new JPanel();
        JLabel imgCaptureLabel = new JLabel(new ImageIcon(img));
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

    private static void initFrame(Container pane) {
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("g(i,j)=α.f(i,j)+β"));
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        sliderPanel.add(emptyPanel);
        sliderPanel.add(new JLabel("Contrast (α)"));
        alphaSlider = new JSlider(0, 30);
        alphaSlider.setMinorTickSpacing(5);
        alphaSlider.setMajorTickSpacing(10);
        alphaSlider.setPaintTicks(true);
        alphaSlider.setPaintLabels(true);
        sliderPanel.add(alphaSlider);
        sliderPanel.add(new JLabel("Brightness (β)"));
        betaslider = new JSlider(-100, 100);
        betaslider.setMinorTickSpacing(10);
        betaslider.setMajorTickSpacing(50);
        betaslider.setPaintTicks(true);
        betaslider.setPaintLabels(true);
        sliderPanel.add(betaslider);
        sliderPanel.add(new JLabel("O = 255.(I / 255)^γ"));
        sliderPanel.add(new JLabel("γ Correction"));
        gammaSlider = new JSlider(0, 30);
        gammaSlider.setMinorTickSpacing(5);
        gammaSlider.setMajorTickSpacing(10);
        gammaSlider.setPaintTicks(true);
        gammaSlider.setPaintLabels(true);
        sliderPanel.add(gammaSlider);

        pane.add(sliderPanel, BorderLayout.PAGE_START);
    }

    private static void update(Mat image) {
        byte[] imageData = new byte[(int) (image.total() * image.channels())];
        image.get(0, 0, imageData);
        byte[] tempImageData = new byte[(int) (newImage.total() * newImage.channels())];
        final int rows = image.rows();
        for (int y = 0; y < rows; y++) {
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
        Mat temp = Mat.zeros(image.size(), image.type());
        temp.put(0, 0, tempImageData);

        Mat lookUpTable = new Mat(1, 256, CvType.CV_8U);
        byte[] lookUpTableData = new byte[(int) (lookUpTable.total() * lookUpTable.channels())];
        for (int i = 0; i < lookUpTable.cols(); i++) {
            lookUpTableData[i] = saturate(Math.pow(i / 255.0, gammaSlider.getValue()) * 255.0);
        }
        lookUpTable.put(0, 0, lookUpTableData);
        Core.LUT(temp, lookUpTable, newImage);

        imgResultLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(newImage)));
        frame.repaint();
    }

    private static byte saturate(double val) {
        int iVal = (int) Math.round(val);
        iVal = iVal > 255 ? 255 : Math.max(iVal, 0);
        return (byte) iVal;
    }

    public static void main(String... args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(ContrastBrightness::run);
    }
}
