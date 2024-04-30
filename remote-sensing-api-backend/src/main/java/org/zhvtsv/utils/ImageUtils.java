package org.zhvtsv.utils;

import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ImageUtils {
    private static final Logger LOG = Logger.getLogger(ImageUtils.class);

    public static Mat getMatRGB(Mat geoTiffImage) {
        if (geoTiffImage.channels() == 4) {
            List<Mat> channels = new ArrayList<>();
            Core.split(geoTiffImage, channels);

            // Keep only the first 3 channels (BGR)
            Mat bgrImage = new Mat();
            Core.merge(channels.subList(0, 3), bgrImage);

            geoTiffImage = bgrImage;
        }
        return geoTiffImage;
    }

    public static Mat readImageFromInputStream(InputStream inputStream) {
        byte[] imageBytes = new byte[0];
        try {
            imageBytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MatOfByte matOfByte = new MatOfByte(imageBytes);
        return Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED);
    }

    public static BufferedImage mat2BufferedImage(Mat mat) {
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", mat, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bufImage;
    }

    public static Mat extractAndDrawBoxesFromInference(Mat mat2D, Mat resizedImage, List<String> classes) {
        HashMap<String, List> result = new HashMap<>();
        result.put("boxes", new ArrayList<Rect2d>());
        result.put("scores", new ArrayList<Double>());
        result.put("class_ids", new ArrayList<Integer>());

        LOG.info("-----Start analysing the inference-----");
        for (int i = 0; i < mat2D.rows(); i++) {
            List<Double> scores = new ArrayList<>();
            for (int j = 4; j < mat2D.cols(); j++) {
                scores.add(mat2D.get(i, j)[0]);
            }
            double maxScore = Collections.max(scores);
            double x = mat2D.get(i, 0)[0] - (0.5 * mat2D.get(i, 2)[0]);
            double y = mat2D.get(i, 1)[0] - (0.5 * mat2D.get(i, 3)[0]);
            double width = mat2D.get(i, 2)[0];
            double height = mat2D.get(i, 3)[0];

            if (maxScore >= 0.25) {
                int classId = argmax(scores);
                Rect2d box = new Rect2d(x, y, width, height);
                result.get("boxes").add(box);
                result.get("scores").add(maxScore);
                result.get("class_ids").add(classId);
            }
        }
        LOG.info("Found classes: " + (ArrayList<Integer>) result.get("class_ids"));
        LOG.info("______End________");
        LOG.info("Boxes preprocessing...");
        ArrayList<Rect2d> boxes = (ArrayList<Rect2d>) result.get("boxes");
        ArrayList<Double> scores = (ArrayList<Double>) result.get("scores");
        ArrayList<Integer> classIds = (ArrayList<Integer>) result.get("class_ids");
        if (classIds.isEmpty()) {
            LOG.info("No objects found");
            throw new NotFoundException("No objects found");
        }

        MatOfRect2d mOfRect = new MatOfRect2d();
        mOfRect.fromList(boxes);
        List<Float> floatScores = scores.stream().map(Double::floatValue).collect(Collectors.toList());

        MatOfFloat mScores = new MatOfFloat(Converters.vector_float_to_Mat(floatScores));
        MatOfInt boxesResult = new MatOfInt();
        Dnn.NMSBoxes(mOfRect, mScores, 0.25f, 0.45f, boxesResult);
        drawBoxesOnTheImage(resizedImage, boxesResult, boxes, classes, classIds, getRandomColorsPerClass(classes));

        return resizedImage;
    }

    private static Mat drawBoxesOnTheImage(Mat img, MatOfInt boxesResult, ArrayList<Rect2d> boxes, List<String> cocoLabels, ArrayList<Integer> classIds, ArrayList<Scalar> colors) {
        List indices_list = boxesResult.toList();
        for (int i = 0; i < boxes.size(); i++) {
            if (indices_list.contains(i)) {
                Rect2d box = boxes.get(i);
                Point x_y = new Point(box.x, box.y);
                Point w_h = new Point(box.x + box.width, box.y + box.height);
                Point text_point = new Point(box.x, box.y - 5);
                Imgproc.rectangle(img, w_h, x_y, new Scalar(0, 165, 255), 1);
                String label = cocoLabels.get(classIds.get(i));
                Imgproc.putText(img, label, text_point, Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 165, 255), 2);
            }
        }
        return img;
    }

    private static ArrayList<Scalar> getRandomColorsPerClass(List<String> classes) {
        Random random = new Random();
        ArrayList<Scalar> colors = new ArrayList<Scalar>();
        for (int i = 0; i < classes.size(); i++) {
            colors.add(new Scalar(new double[]{random.nextInt(255), random.nextInt(255), random.nextInt(255)}));
        }
        return colors;
    }

    /**
     * Returns index of maximum element in the list
     */
    private static int argmax(List<Double> array) {
        double max = array.get(0);
        int re = 0;
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) > max) {
                max = array.get(i);
                re = i;
            }
        }
        return re;
    }

}
