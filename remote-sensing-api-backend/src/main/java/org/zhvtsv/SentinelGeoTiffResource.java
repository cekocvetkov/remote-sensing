package org.zhvtsv;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.opencv.core.Mat;
import org.zhvtsv.service.ObjectDetection;
import org.zhvtsv.service.sentinel.SentinelProcessApiClient;
import org.zhvtsv.service.sentinel.SentinelRequest;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import static org.zhvtsv.utils.ImageUtils.readImageFromInputStream;
import static org.zhvtsv.utils.ImageUtils.mat2BufferedImage;

@Path("/api/v1/sentinel")
public class SentinelGeoTiffResource {
    private static final Logger LOG = Logger.getLogger(SentinelGeoTiffResource.class);
    @Inject
    SentinelProcessApiClient sentinelProcessApiClient;
    @Inject
    ObjectDetection yolovObjectDetection;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"image/png", "image/jpg", "image/tiff"})
    public Response getGeoTiffSentinel(SentinelRequest sentinelRequest) {
        LOG.info("Request for GeoTiffs " + sentinelRequest);

        InputStream inputStream = sentinelProcessApiClient.getGeoTiff(sentinelRequest);
        Mat res = yolovObjectDetection.detectObjectOnImage(readImageFromInputStream(inputStream), sentinelRequest.getModel());
        BufferedImage image = mat2BufferedImage(res);
        return Response.ok(image).build();
    }

}
