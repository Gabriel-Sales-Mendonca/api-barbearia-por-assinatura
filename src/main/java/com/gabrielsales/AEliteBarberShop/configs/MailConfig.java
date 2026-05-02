package com.gabrielsales.AEliteBarberShop.configs;

import com.gabrielsales.AEliteBarberShop.dtos.out.SendEmailRequestDTO;
import com.gabrielsales.AEliteBarberShop.dtos.out.SenderOrRecipientDTO;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RegisterReflectionForBinding({
        SendEmailRequestDTO.class,
        SenderOrRecipientDTO.class
})
public class MailConfig {

    @Bean
    public RestClient mailerSendClient(@Value("${mailersend.api.token}") String token) {
        return RestClient.builder()
                .baseUrl("https://api.mailersend.com/v1")
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
