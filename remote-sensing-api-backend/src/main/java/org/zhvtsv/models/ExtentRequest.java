package org.zhvtsv.models;

import java.util.Arrays;

public class ExtentRequest {
    private String id;
    private double [] extent;
    private String model;
    private String dateFrom;
    private String dateTo;
    private int cloudCoverage;

    public double[] getExtent() {
        return extent;
    }

    public void setExtent(double[] extent) {
        this.extent = extent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public int getCloudCoverage() {
        return cloudCoverage;
    }

    public void setCloudCoverage(int cloudCoverage) {
        this.cloudCoverage = cloudCoverage;
    }

    @Override
    public String toString() {
        return "ExtentRequest{" +
                "id='" + id + '\'' +
                ", extent=" + Arrays.toString(extent) +
                ", model='" + model + '\'' +
                ", dateFrom='" + dateFrom + '\'' +
                ", dateTo='" + dateTo + '\'' +
                ", cloudCoverage='" + cloudCoverage +
                '}';
    }
}
