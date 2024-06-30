package org.zhvtsv.utils;

import org.jboss.logging.Logger;
import org.zhvtsv.exception.DataReadException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathUtils {
    private static final Logger LOG = Logger.getLogger(PathUtils.class);

    public static String getPathForImageInResources(String imageName) {
        Path tempPath = null;
        try {
            InputStream in = PathUtils.class.getResourceAsStream("/" + imageName);
            tempPath = Files.createTempFile(imageName, ".onnx");
            Files.copy(in, tempPath);
            return tempPath.toString();
//            return Paths.get(PathUtils.class.getClassLoader().getResource(imageName).toURI()).toString();
        } catch (IOException e) {
            LOG.error("Error on reading data", e);
            throw new DataReadException("Error on reading data");
        } finally {
            try {
                Files.delete(tempPath);
            } catch (IOException e) {
                LOG.error("Error on deleting temp data", e);
                throw new DataReadException("Error on deleting temp data");
            }
        }
    }
}