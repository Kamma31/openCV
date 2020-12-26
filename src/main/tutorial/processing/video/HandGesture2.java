package main.tutorial.processing.video;

import com.bulenkov.iconloader.util.Pair;
import lombok.AllArgsConstructor;
import main.misc.Util;
import main.opencv.core.Partition;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.opencv.imgproc.Imgproc.*;

public class HandGesture2 {
    private static final String WINDOW_NAME = "Simple hand gesture Detection";
    private static Mat src;
    private static VideoCapture cap;
    private static CaptureTask captureTask;
    private static JFrame frame;
    private static JLabel imgLabel;
    private static JCheckBox checkbox;
    private static JSlider maxSlider;
    private static JSlider maxAngleSlider;
    private static BackgroundSubtractor backSubKNN;

    public static void run() {
        src = new Mat();
        cap = new VideoCapture(0);
        cap.read(src);
        backSubKNN = Video.createBackgroundSubtractorMOG2();
        frame = new JFrame(WINDOW_NAME);
        initFrame(frame.getContentPane());

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                HandGesture2.captureTask.cancel(true);
            }
        });

        final Image imgSrc = HighGui.toBufferedImage(src);
        final JPanel framePanel = new JPanel();
        imgLabel = new JLabel(new ImageIcon(imgSrc));
        framePanel.add(imgLabel);
        frame.add(framePanel, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);

        captureTask = new CaptureTask();
        captureTask.execute();
    }

    private static void initFrame(final Container pane) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(new JLabel("Max dist"));
        maxSlider = new JSlider(0, 100, 20);
        maxSlider.setMajorTickSpacing(10);
        maxSlider.setMinorTickSpacing(5);
        maxSlider.setPaintTicks(true);
        maxSlider.setPaintLabels(true);
        panel.add(maxSlider);

        panel.add(new JLabel("Max angle"));
        maxAngleSlider = new JSlider(0, 90, 60);
        maxAngleSlider.setMajorTickSpacing(10);
        maxAngleSlider.setMinorTickSpacing(5);
        maxAngleSlider.setPaintTicks(true);
        maxAngleSlider.setPaintLabels(true);
        panel.add(maxAngleSlider);

        checkbox = new JCheckBox("See binary mask");
        checkbox.setSelected(true);
        panel.add(checkbox);
        pane.add(panel, BorderLayout.WEST);

    }

    private static class CaptureTask extends SwingWorker<Void, Mat> {
        static final Scalar BLUE = new Scalar(255, 0, 0);
        static final Scalar GREEN = new Scalar(0, 255, 0);
        static final Scalar RED = new Scalar(0, 0, 255);

        static final BiFunction<Point, Point, Double> getDist = (pt1, pt2) -> Math.sqrt(Math.pow(pt1.x - pt2.x, 2) + Math.pow(pt1.y - pt2.y, 2));

        static final BiPredicate<Point, Point> ptsBelongToSameCluster = (pt1, pt2) -> getDist.apply(pt1, pt2) < HandGesture2.maxSlider.getValue();

        static final Function<List<Point>, Point> getCentralPoint = pts -> {
            final Point reduce = pts.stream().reduce(new Point(0, 0), (p1, p2) -> new Point(p1.x + p2.x, p1.y + p2.y));
            return new Point(reduce.x / pts.size(), reduce.y / pts.size());
        };

        @AllArgsConstructor
        static class Angle {
            Point pt;
            Point d1;
            Point d2;
        }

        @Override
        protected Void doInBackground() {
            final Mat matFrame = new Mat();
            while (!isCancelled()) {
                if (!HandGesture2.cap.read(matFrame)) {
                    break;
                }
                publish(matFrame.clone());
            }
            return null;
        }

        @Override
        protected void process(final List<Mat> frames) {
            src = frames.get(frames.size() - 1);

            final Mat threshed = processImg();
            final MatOfPoint contour = getContours(threshed);
            if (contour == null) {
                return;
            }
            if (checkbox.isSelected()) {
                update(threshed);
            } else {
                Imgproc.drawContours(src, Collections.singletonList(contour), -1, GREEN);

                // Liste des points definissant le contour grossier
                final List<Pair<Integer, Point>> hull = getRoughHull(contour);
                final List<Integer> hullIndices = hull.stream().map(pair -> pair.getFirst()).collect(Collectors.toList());
                final MatOfPoint hullPoints = new MatOfPoint();
                hullPoints.fromList(hull.stream().map(pair -> pair.getSecond()).collect(Collectors.toList()));
//                Imgproc.drawContours(src, Collections.singletonList(hullPoints), -1, RED);

                // On trouves les differents sommets de la main (bout du doigt et ses deux creux)
                final List<Angle> hullDefectVertices = getHullDefectVertices(contour, hullIndices);
                // Affichage de tous les angles detectes
//                hullDefectVertices.forEach(angle -> {
//                    Imgproc.circle(src, angle.pt, 5, BLUE, 2);
//                    Imgproc.circle(src, angle.d1, 5, GREEN, 2);
//                    Imgproc.circle(src, angle.d2, 5, RED, 2);
//                });
                // On filtre seulement les angles correspondant à un doigt
                final List<Angle> validVertices = filterVerticesByAngles(hullDefectVertices);

                // Affichage des angles filtres
                validVertices.forEach(v -> {
                    Imgproc.line(src, v.pt, v.d1, RED);
                    Imgproc.line(src, v.pt, v.d2, RED);
                    Imgproc.ellipse(src, new RotatedRect(v.pt, new Size(20, 20), 0), RED);
                });

                // On compte et on affiche le nombre de doigts
                final int numFingerUp = validVertices.size();
                Imgproc.rectangle(src, new Point(10, 10), new Point(70, 70), BLUE);
                Imgproc.putText(src, String.valueOf(numFingerUp), new Point(20, 60), FONT_ITALIC, 2, BLUE);

                update(src);
            }
        }

        private static Mat processImg() {
            // Utilisation de la suppression de background pour détailler la main
            final Mat res = new Mat();
            backSubKNN.apply(src, res, 0);
            // Un petit flitrage pour éliminer le bruit
            Imgproc.blur(res, res, new Size(10, 10));
            // On assure le bon découpage de la main
            Imgproc.threshold(res, res, 200, 255, THRESH_BINARY_INV);
            // Espace lumineux ? On prend alors le négatif de l'image
            Core.bitwise_not(res, res);

            return res;
        }

        /**
         * Retourne un polygon a partir du contour de la forme
         * Il n'y aura qu'un seul point pour un voisinnage
         *
         * @param contour Contour de la forme
         * @return liste des indices des points des angles du polygone
         */
        private static List<Pair<Integer, Point>> getRoughHull(final MatOfPoint contour) {
            final MatOfInt hullIndices = new MatOfInt();
            try {
                // En cas de recoupement dans le contour ca pose des soucis
                Imgproc.convexHull(contour, hullIndices);
            } catch (final Exception ignored) {
                return new ArrayList<>();
            }
            final List<Point> contourPoints = contour.toList();
            final List<Pair<Integer, Point>> hullPointsWithIdx = new ArrayList<>();
            hullIndices.toList().forEach(idx -> hullPointsWithIdx.add(Pair.create(idx, contourPoints.get(idx))));
            final List<Point> hullPoints = hullPointsWithIdx.stream().map(pair -> pair.getSecond()).collect(Collectors.toList());

            // On regroupe les point dans le même voisinnage
            final List<Integer> labels = new ArrayList<>();
            Partition.partition(hullPoints, labels, ptsBelongToSameCluster);
            final Map<Integer, List<Point>> pointsByLabel = new HashMap<>();
            labels.forEach(label -> pointsByLabel.put(label, new ArrayList<>()));
            for (int i = 0; i < hullPointsWithIdx.size(); i++) {
                final Integer label = labels.get(i);
                pointsByLabel.get(label).add(hullPoints.get(i));
            }

            // On retourne les points centraux de chaque voisinnage
            final List<Point> centralPoints = new ArrayList<>(pointsByLabel.values()).stream().map(CaptureTask::getMostCentralPoint).collect(Collectors.toList());
            return centralPoints.stream()
                    .map(pt ->
                            hullPointsWithIdx.stream().filter(pair -> pair.getSecond() == pt).collect(Collectors.toList()).get(0)
                    ).collect(Collectors.toList());
        }

        private static List<Angle> getHullDefectVertices(final MatOfPoint contour, final List<Integer> hullIndices) {
            // On trouves les points anguleux de notre contour
            final MatOfInt hullMatIndices = new MatOfInt();
            hullMatIndices.fromList(hullIndices);
            final MatOfInt4 defects = new MatOfInt4();
            Imgproc.convexityDefects(contour, hullMatIndices, defects);

            final Map<Integer, List<Integer>> hullPointDefectNeighbors = new HashMap<>();
            hullIndices.forEach(idx -> hullPointDefectNeighbors.put(idx, new ArrayList<>()));

            for (int i = 0; i < defects.rows(); i++) {
                final double[] defect = defects.get(i, 0);
                final int startPtIdx = (int) defect[0];
                final int endPtIdx = (int) defect[1];
                final int farthestPtIdx = (int) defect[2];

//                Imgproc.circle(src, contour.toList().get(startPtIdx), 5, BLUE, 2);
//                Imgproc.circle(src, contour.toList().get(endPtIdx), 5, GREEN, 2);
//                Imgproc.circle(src, contour.toList().get(farthestPtIdx), 5, RED, 2);

                hullPointDefectNeighbors.get(startPtIdx).add(farthestPtIdx);
                hullPointDefectNeighbors.get(endPtIdx).add(farthestPtIdx);
            }

            // On retourne un ensemble (bout, creux) au lieu de (creu, bouts) pour permettre une meilleure detection des doigts
            return hullPointDefectNeighbors.keySet()
                    .stream()
                    .filter(hullIndex -> hullPointDefectNeighbors.get(hullIndex).size() > 1)
                    .map(hullIndex -> {
                        final List<Integer> gapPts = hullPointDefectNeighbors.get(hullIndex);
                        return new Angle(contour.toList().get(hullIndex), contour.toList().get(gapPts.get(0)), contour.toList().get(gapPts.get(1)));
                    }).collect(Collectors.toList());
        }

        private static MatOfPoint getContours(final Mat threshed) {
            final List<MatOfPoint> contours = new ArrayList<>();
            // On trouve les contours exitants
            Imgproc.findContours(threshed, contours, new Mat(), RETR_TREE, CHAIN_APPROX_TC89_L1);
            // On garde celui qui possède le plus de point ie. le plus grand
            final Optional<MatOfPoint> max = contours.stream().max(Comparator.comparingInt(Mat::rows));
            return max.orElse(null);
        }

        private static Point getMostCentralPoint(final List<Point> pts) {
            final Point centralPoint = getCentralPoint.apply(pts);
            return pts.stream().sorted(Comparator.comparingDouble(pt -> getDist.apply(pt, centralPoint))).toArray(Point[]::new)[0];
        }

        private static List<Angle> filterVerticesByAngles(final List<Angle> vertices) {
            final double max = Math.cos(maxAngleSlider.getValue() * Math.PI / 180);
            return vertices.stream().filter(v -> {
                final double a = getDist.apply(v.d1, v.d2);
                final double b = getDist.apply(v.pt, v.d1);
                final double c = getDist.apply(v.pt, v.d2);
                final double angle = (Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * b * c);

                return max < angle;
            }).collect(Collectors.toList());
        }

        private static void update(final Mat img) {
            imgLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(img)));
            frame.repaint();
        }

    }

    public static void main(final String[] args) {
        Util.loadLibrairies();
        SwingUtilities.invokeLater(HandGesture2::run);
    }
}
