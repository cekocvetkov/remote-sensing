package org.zhvtsv.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.jboss.logging.Logger;
import org.zhvtsv.exception.BadGatewayException;
import org.zhvtsv.exception.DataReadException;

@ApplicationScoped
public class TreeDetectionDeepforest implements IDetectionService{
    private static final Logger LOG = Logger.getLogger(TreeDetectionDeepforest.class);

    @ConfigProperty(name = "deepforest.url")
    String deepForestURL;

    public Mat detectObjectOnImage(Mat image, String model) {
        LOG.info("Detect with deepforest "+model);
        MatOfByte matOfByte = new MatOfByte();
        
        Imgcodecs.imencode(".tif", image, matOfByte);
        byte[] img = matOfByte.toArray();
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = deepForestURL+"/"+model;

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "multipart/form-data")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(img))
                    .build();
        } catch (URISyntaxException e) {
            LOG.error("Cannot read URI for deepforest service",e);
            throw new DataReadException("Cannot read URI for deepforest service");
        }

        HttpResponse<byte[]> response = null;
        Mat responseMat = new Mat();
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            MatOfByte responseMatBytes = new MatOfByte(response.body());
            responseMat = Imgcodecs.imdecode(responseMatBytes, Imgcodecs.IMREAD_UNCHANGED);
        } catch (IOException | InterruptedException e) {
            LOG.error("Error on calling the deepforest service", e);
            throw new BadGatewayException("Error on calling the deepforest service");
        }

        LOG.info("Response code from deepforest microservice: " + response.statusCode());

        return responseMat;
    }

}
