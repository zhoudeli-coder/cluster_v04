package com.example.product.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource.druid.stat-view-servlet")
public class StatViewServletProperties {

    private String urlPattern;
    private String allow;
    private String loginUsername;
    private String loginPassword;
    private String deny;
    private Boolean resetEnable;
    private Boolean enabled;
}
