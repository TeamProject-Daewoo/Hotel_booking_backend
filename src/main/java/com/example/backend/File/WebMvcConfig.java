package com.example.backend.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir-default}")
    private String defaultUploadDir;

    @Value("${file.upload-dir-alt}")
    private String altUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // file:/C:/uploads/ 및 file:/D:/uploads/ 두 경로 모두 매핑
        String pathDefault = "file:" + defaultUploadDir + "/";
        String pathAlt = "file:" + altUploadDir + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(pathDefault, pathAlt);
    }
}
