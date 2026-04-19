package com.gabrielsales.AEliteBarberShop.services.exceptions;

public class InvalidOrExpiredTokenException extends RuntimeException {
    public InvalidOrExpiredTokenException(String message) {
        super(message);
    }
}
