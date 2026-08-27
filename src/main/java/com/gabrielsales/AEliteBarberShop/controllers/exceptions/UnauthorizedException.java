package com.gabrielsales.AEliteBarberShop.controllers.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
