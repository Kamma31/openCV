package main.frames;

import lombok.Getter;
import main.misc.Util;

import javax.swing.*;
import java.awt.*;

public class SliderHSVPanel {
    private static final String LOW_H_NAME = "Low H";
    private static final String LOW_S_NAME = "Low S";
    private static final String LOW_V_NAME = "Low V";
    private static final String HIGH_H_NAME = "High H";
    private static final String HIGH_S_NAME = "High S";
    private static final String HIGH_V_NAME = "High V";
    private final JSlider sliderLowH;
    private final JSlider sliderHighH;
    private final JSlider sliderLowS;
    private final JSlider sliderHighS;
    private final JSlider sliderLowV;
    private final JSlider sliderHighV;

    @Getter
    private final JPanel panel;

    public SliderHSVPanel() {
        this(0, 255, 0, 255, 0, 255);
    }

    public SliderHSVPanel(final int lowH, final int highH, final int lowS, final int highS, final int lowV, final int highV) {
        Util.loadLibrairies();
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel(LOW_H_NAME), BorderLayout.CENTER);
        final int maxValueH = 360 / 2;
        sliderLowH = new JSlider(0, maxValueH, lowH);
        sliderLowH.setMajorTickSpacing(50);
        sliderLowH.setMinorTickSpacing(10);
        sliderLowH.setPaintTicks(true);
        sliderLowH.setPaintLabels(true);
        panel.add(sliderLowH, BorderLayout.CENTER);
        panel.add(new JLabel(HIGH_H_NAME), BorderLayout.CENTER);
        sliderHighH = new JSlider(0, maxValueH, highH);
        sliderHighH.setMajorTickSpacing(50);
        sliderHighH.setMinorTickSpacing(10);
        sliderHighH.setPaintTicks(true);
        sliderHighH.setPaintLabels(true);
        panel.add(sliderHighH, BorderLayout.CENTER);
        panel.add(new JLabel(LOW_S_NAME), BorderLayout.CENTER);
        final int maxValue = 255;
        sliderLowS = new JSlider(0, maxValue, lowS);
        sliderLowS.setMajorTickSpacing(50);
        sliderLowS.setMinorTickSpacing(10);
        sliderLowS.setPaintTicks(true);
        sliderLowS.setPaintLabels(true);
        panel.add(sliderLowS, BorderLayout.CENTER);
        panel.add(new JLabel(HIGH_S_NAME), BorderLayout.CENTER);
        sliderHighS = new JSlider(0, maxValue, highS);
        sliderHighS.setMajorTickSpacing(50);
        sliderHighS.setMinorTickSpacing(10);
        sliderHighS.setPaintTicks(true);
        sliderHighS.setPaintLabels(true);
        panel.add(sliderHighS, BorderLayout.CENTER);
        panel.add(new JLabel(LOW_V_NAME), BorderLayout.CENTER);
        sliderLowV = new JSlider(0, maxValue, lowV);
        sliderLowV.setMajorTickSpacing(50);
        sliderLowV.setMinorTickSpacing(10);
        sliderLowV.setPaintTicks(true);
        sliderLowV.setPaintLabels(true);
        panel.add(sliderLowV, BorderLayout.CENTER);
        panel.add(new JLabel(HIGH_V_NAME), BorderLayout.CENTER);
        sliderHighV = new JSlider(0, maxValue, highV);
        sliderHighV.setMajorTickSpacing(50);
        sliderHighV.setMinorTickSpacing(10);
        sliderHighV.setPaintTicks(true);
        sliderHighV.setPaintLabels(true);
        panel.add(sliderHighV, BorderLayout.CENTER);
        sliderLowH.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valH = Math.min(sliderHighH.getValue() - 1, source.getValue());
            sliderLowH.setValue(valH);
        });
        sliderHighH.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valH = Math.max(source.getValue(), sliderLowH.getValue() + 1);
            sliderHighH.setValue(valH);
        });
        sliderLowS.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valS = Math.min(sliderHighS.getValue() - 1, source.getValue());
            sliderLowS.setValue(valS);
        });
        sliderHighS.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valS = Math.max(source.getValue(), sliderLowS.getValue() + 1);
            sliderHighS.setValue(valS);
        });
        sliderLowV.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valV = Math.min(sliderHighV.getValue() - 1, source.getValue());
            sliderLowV.setValue(valV);
        });
        sliderHighV.addChangeListener(e -> {
            final JSlider source = (JSlider) e.getSource();
            final int valV = Math.max(source.getValue(), sliderLowV.getValue() + 1);
            sliderHighV.setValue(valV);
        });
    }

    public int getSliderLowHValue() {
        return sliderLowH.getValue();
    }

    public int getSliderHighHValue() {
        return sliderHighH.getValue();
    }

    public int getSliderLowSValue() {
        return sliderLowS.getValue();
    }

    public int getsliderHighSValue() {
        return sliderHighS.getValue();
    }

    public int getsliderLowVValue() {
        return sliderLowV.getValue();
    }

    public int getsliderHighVValue() {
        return sliderHighV.getValue();
    }

}
