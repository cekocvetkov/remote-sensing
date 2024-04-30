package org.zhvtsv.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.zhvtsv.utils.ImageUtils.extractAndDrawBoxesFromInference;
import static org.zhvtsv.utils.ImageUtils.getMatRGB;

@ApplicationScoped
public class ObjectDetection implements IDetectionService {
    private static final Logger LOG = Logger.getLogger(ObjectDetection.class);
    private static final String CLASSES_PATH = "yolov8ObjectDetectionDiorClasses.txt";
    private static final int IMG_SIZE = 640;
    @ConfigProperty(name = "models.path")
    String modelsPath;

    public Mat detectObjectOnImage(Mat geoTiffImage, String model) {
        // Convert to 3 channels (RGB) if it has an alpha channel
        geoTiffImage = getMatRGB(geoTiffImage);
        Mat resizedImage = new Mat();

        //Yolov8 img size is 800
        int imageSize = model.startsWith("yolov8") ? 800 : IMG_SIZE;

        Imgproc.resize(geoTiffImage, resizedImage, new Size(imageSize, imageSize), Imgproc.INTER_AREA);

        Mat inputBlob = Dnn.blobFromImage(resizedImage, 1.0 / 255.0, new Size(imageSize, imageSize), // Here we supply the spatial size that the Convolutional Neural Network expects.
                new Scalar(new double[]{0.0, 0.0, 0.0}), true, false);
        System.out.println(model);
//        Net dnnNet = Dnn.readNetFromONNX(PathUtils.getPathForImageInResources(model + ".onnx"));
        Net dnnNet = Dnn.readNetFromONNX(modelsPath + model + ".onnx");
        LOG.info("DNN from ONNX was successfully loaded!");

        dnnNet.setInput(inputBlob);

        Mat outputs = dnnNet.forward();

        Mat mat2D = outputs.reshape(1, (int) outputs.size().width); // The second parameter is the number of rows

        Core.transpose(mat2D, mat2D);
        LOG.info("Output: " + mat2D.rows() + " x " + mat2D.cols());

        resizedImage = extractAndDrawBoxesFromInference(mat2D, resizedImage, getClasses(model));

        LOG.info("Done.");
        return resizedImage;
    }


    private ArrayList<String> getClasses(String model) {
        ArrayList<String> imgLabels;
        String classesPath = CLASSES_PATH;
        if (model.startsWith("yolov9")) {
            classesPath = "cocoClasses.txt";
        }
        if (model.startsWith("project")) {
            classesPath = "project.txt";
        }
        if (model.startsWith("robotic")) {
            classesPath = "robotic.txt";
        }

        try (Stream<String> lines = Files.lines(Path.of(modelsPath + classesPath))) {
            imgLabels = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return imgLabels;
    }

}
