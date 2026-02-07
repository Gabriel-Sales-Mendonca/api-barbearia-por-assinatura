package com.gabrielsales.AEliteBarberShop.services.exceptions;

public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String message) {
        super(message);
    }
}
