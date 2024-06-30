package org.zhvtsv.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;
import org.zhvtsv.exception.DataReadException;
import org.zhvtsv.exception.NotFoundHttpException;

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

        //        Net dnnNet = Dnn.readNetFromONNX(PathUtils.getPathForImageInResources(model + ".onnx"));
        Net dnnNet = readNetFromONNXModel(model);

        dnnNet.setInput(inputBlob);

        Mat outputs = dnnNet.forward();

        Mat mat2D = outputs.reshape(1, (int) outputs.size().width); // The second parameter is the number of rows

        Core.transpose(mat2D, mat2D);
        LOG.info("Output: " + mat2D.rows() + " x " + mat2D.cols());

        resizedImage = extractAndDrawBoxesFromInference(mat2D, resizedImage, getClasses(model));

        LOG.info("Done.");
        return resizedImage;
    }

    private Net readNetFromONNXModel(String model) {
        Net dnnNet = null;
        try {
            dnnNet = Dnn.readNetFromONNX(modelsPath + model + ".onnx");
        } catch ( CvException ex ) {
            LOG.error("Cannot read detection model file", ex);
            throw new ServerErrorException("Cannot read detection model file", Response.Status.INTERNAL_SERVER_ERROR);
        }
        if(dnnNet == null || dnnNet.empty()) {
            throw new ServerErrorException("There was an unexpected problem with detection model file", Response.Status.INTERNAL_SERVER_ERROR);
        }
        LOG.info("DNN from ONNX was successfully loaded!");
        return dnnNet;
    }

    private ArrayList<String> getClasses(String model) {
        ArrayList<String> imgLabels;
        String classesPath = CLASSES_PATH;

        if (model.startsWith("robotic")) {
            classesPath = "robotic.txt";
        }
        if (model.startsWith("aspire")) {
            classesPath = "aspireClasses.txt";
        }

        try (Stream<String> lines = Files.lines(Path.of(modelsPath + classesPath))) {
            imgLabels = lines.collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            LOG.error("Error on reading model's class files", e);
            throw new DataReadException("Error on reading model's class files");
        }
        return imgLabels;
    }

}
