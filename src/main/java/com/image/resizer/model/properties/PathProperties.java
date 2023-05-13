package com.image.resizer.model.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "path")
public class PathProperties {

    private String video;

    private String thumbnail;
}
