package com.paipeng.idcard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:version.properties"})
public class VersionConfig {
    //spring will automatically bind value of property
    @Value("${app.version}")
    private String version;

    @Value("${app.name}")
    private String name;

    @Value("${app.create_date}")
    private String createData;

    //this bean needed to resolve ${property.name} syntax


    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getCreateData() {
        return createData;
    }
}
