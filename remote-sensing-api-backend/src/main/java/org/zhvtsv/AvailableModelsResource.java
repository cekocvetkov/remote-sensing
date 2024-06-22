package org.zhvtsv;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.zhvtsv.utils.PathUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/api/v1/remote-sensing/models")
public class AvailableModelsResource {
    private static final Logger LOG = Logger.getLogger(RemoteSensingResource.class);
    @ConfigProperty(name = "models.json")
    String availableModelsPath;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> getAvailableModels() {
        System.out.println("!!!!!");
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(availableModelsPath)) {
            return mapper.readValue(is, List.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }
}
