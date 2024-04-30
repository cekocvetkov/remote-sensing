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

@ApplicationScoped
public class TreeDetectionDeepforest {
    private static final Logger LOG = Logger.getLogger(TreeDetectionDeepforest.class);

    @ConfigProperty(name = "deepforest.url")
    String deepForestURL;

    public byte[] detectObjectOnImage(Mat image, String model) {
        LOG.info("Detect with deepforest "+model);
        MatOfByte matOfByte = new MatOfByte();
        
        Imgcodecs.imencode(".tif", image, matOfByte);
        byte[] img = matOfByte.toArray();
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = "http://deepforest:8081/deepforest/"+model;
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-Type", "multipart/form-data")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(img))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<byte[]> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Response code from deepforest microservice: " + response.statusCode());

        return response.body();
    }

}
