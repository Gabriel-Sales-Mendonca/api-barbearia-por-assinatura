package com.gabrielsales.AEliteBarberShop.dtos;

public record ValidateVerificationSignatureTokenResponseDTO(
    String userName,
    String userLastname,
    SignatureResponseDTO signatureResponseDTO
) {
}
