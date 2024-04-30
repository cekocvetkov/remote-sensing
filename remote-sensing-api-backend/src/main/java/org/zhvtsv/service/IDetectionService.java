package org.zhvtsv.service;

import org.opencv.core.Mat;

public interface IDetectionService {
    public Mat detectObjectOnImage(Mat image, String model);
}
