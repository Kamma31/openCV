package main;

import main.frames.MainFrame;
import main.misc.Tasks;
import main.misc.Util;
import main.test.Test;
import main.tutorial.processing.image.AddingImages;
import main.tutorial.processing.image.ErodeDilate;
import main.tutorial.processing.image.FaceDetection;
import main.tutorial.morphology.Morphology;
import main.tutorial.morphology.Smoothing;
import main.tutorial.mask.MatMask;
import main.tutorial.pixeltransformation.ContrastBrightness;
import main.tutorial.theorics.DiscreteFourierTransform;
import org.opencv.highgui.HighGui;

public class Main {

    public static void main(String[] args) {
        Util.loadLibrairies();

        MainFrame mainFrame = new MainFrame();
        mainFrame.getBtn().addActionListener(e -> {
            HighGui.destroyAllWindows();
            String selectedTask = mainFrame.getCombo().getSelectedItem().toString();
            final Tasks task = Tasks.valueOf(selectedTask);
            switch (task) {
                case TEST:
                    Test.run();
                    break;
                case MAT_MASK:
                    MatMask.run();
                    break;
                case FACE_DETECTION:
                    FaceDetection.run();
                    break;
                case LINEAR_BLEND:
                    AddingImages.run();
                    break;
                case CONTRAST:
                    ContrastBrightness.run();
                    break;
                case DFT:
                    DiscreteFourierTransform.run();
                    break;
                case SMOOTHING:
                    Smoothing.run();
                    break;
                case ERODE_DILATE:
                    ErodeDilate.run("LinuxLogo.jpg");
                    ErodeDilate.run("cat.jpg");
                    break;
                case OTHERS:
                    Morphology.run();
                    break;
                default:
                    break;
            }
            mainFrame.requestFocus();
            if (task.isClickableTask()) {
                mainFrame.resetCombo();
            }
        });
    }
}
