package com.paipeng.idcard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@ConfigurationProperties()
public class ApplicationConfig {
    @Value("${license.private.key.file}")
    private String licensePrivateKeyFile;

    @Value("${license.public.key.file}")
    private String licensePublicKeyFile;

    @Bean
    public ResourceBundleMessageSource messageSource() {

        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("messages/string");
        source.setUseCodeAsDefaultMessage(true);

        return source;
    }

    public String getLicensePrivateKeyFile() {
        return licensePrivateKeyFile;
    }

    public String getLicensePublicKeyFile() {
        return licensePublicKeyFile;
    }
}
