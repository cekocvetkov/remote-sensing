package org.zhvtsv.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PathUtils {
    public static String getPathForImageInResources(String imageName) {
        Path tempPath = null;
        try {
            System.out.println(PathUtils.class.getClassLoader().getResource(imageName));
//            System.out.println(PathUtils.class.getClassLoader().);
            InputStream in = PathUtils.class.getResourceAsStream("/" + imageName);
            tempPath = Files.createTempFile(imageName, ".onnx");
            System.out.println(tempPath);
            Files.copy(in, tempPath);
            return tempPath.toString();
//            return Paths.get(PathUtils.class.getClassLoader().getResource(imageName).toURI()).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                Files.delete(tempPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}