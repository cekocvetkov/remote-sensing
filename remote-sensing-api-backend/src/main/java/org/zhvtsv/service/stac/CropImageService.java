package org.zhvtsv.service.stac;

import jakarta.enterprise.context.ApplicationScoped;
import org.geotools.api.geometry.Bounds;
import org.geotools.api.parameter.ParameterValueGroup;
import org.geotools.api.referencing.FactoryException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.processing.CoverageProcessor;
import org.geotools.coverage.processing.Operations;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

@ApplicationScoped
public class CropImageService {
    public GridCoverage2D cropGeoTiff(GridCoverage2D geoTiff, double [] extent) {
        try {
            geoTiff = (GridCoverage2D) Operations.DEFAULT.resample(geoTiff, CRS.decode("EPSG:4326"));
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }

        ReferencedEnvelope envelope = null;
        try {
            envelope = new ReferencedEnvelope(  extent[1],extent[3], extent[0], extent[2] , CRS.decode("EPSG:4326"));
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }

        return cropCoverage(geoTiff, envelope);
    }

    private GridCoverage2D cropCoverage(GridCoverage2D gridCoverage, Bounds envelope) {
        CoverageProcessor processor = CoverageProcessor.getInstance();

        ParameterValueGroup param = processor.getOperation("CoverageCrop").getParameters();
        param.parameter("Source").setValue(gridCoverage);
        param.parameter("Envelope").setValue(envelope);

        return (GridCoverage2D) processor.doOperation(param);
    }
}
