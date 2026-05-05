package com.gabrielsales.AEliteBarberShop.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CloudinaryResponseDTO(
        @JsonProperty("public_id") String publicId,
        @JsonProperty("secure_url") String secureUrl,
        @JsonProperty("format") String format
) {
}
