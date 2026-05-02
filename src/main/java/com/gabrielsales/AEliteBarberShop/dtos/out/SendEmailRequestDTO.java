package com.gabrielsales.AEliteBarberShop.dtos.out;

import java.util.List;

public record SendEmailRequestDTO(
        SenderOrRecipientDTO from,
        List<SenderOrRecipientDTO> to,
        String subject,
        String text,
        String html
) {
}
