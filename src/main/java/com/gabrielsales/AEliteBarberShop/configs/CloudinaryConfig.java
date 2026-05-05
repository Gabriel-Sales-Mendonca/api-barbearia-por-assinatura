package com.gabrielsales.AEliteBarberShop.configs;

import com.gabrielsales.AEliteBarberShop.dtos.CloudinaryResponseDTO;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Configuration
@RegisterReflectionForBinding({
        CloudinaryResponseDTO.class
})
public class CloudinaryConfig {

    @Value("${cloudinary.api.url}")
    private String baseUrl;

    @Value("${cloudinary.api.key}")
    private String apiKey;

    @Value("${cloudinary.api.secret}")
    private String apiSecret;

    @Bean("cloudinaryClient")
    public RestClient cloudinaryClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Basic " +
                        Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes()))
                .build();
    }

}
