package com.epicode.EASYPLAY.configuration;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary uploader() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dlo86p6cf");
        config.put("api_key", "157518234845816");
        config.put("api_secret", "z-RB-xK49aawD33m-UaCScVvm0c");
        return new Cloudinary(config);
    }
}
