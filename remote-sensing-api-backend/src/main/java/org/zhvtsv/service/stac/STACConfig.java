package org.zhvtsv.service.stac;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "stac-client")
public interface STACConfig {
    String url();
}
