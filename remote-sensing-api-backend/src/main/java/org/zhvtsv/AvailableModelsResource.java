package org.zhvtsv;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.zhvtsv.exception.DataReadException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Path("/api/v1/remote-sensing/models")
public class AvailableModelsResource {
    private static final Logger LOG = Logger.getLogger(AvailableModelsResource.class);
    @ConfigProperty(name = "models.json")
    String availableModelsPath;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> getAvailableModels() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(availableModelsPath)) {
            return mapper.readValue(is, List.class);
        } catch (IOException e) {
            LOG.error("Failed to read json models", e);
            throw new DataReadException("Failed to read models JSON File");
        }
    }
}
