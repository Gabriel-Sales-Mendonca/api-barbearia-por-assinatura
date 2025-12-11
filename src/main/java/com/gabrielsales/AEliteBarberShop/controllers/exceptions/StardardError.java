package com.gabrielsales.AEliteBarberShop.controllers.exceptions;

import java.time.Instant;

public class StardardError {

    private Instant timestamp;
    private Integer status;
    private String errorName;
    private String message;
    private String path;

    public StardardError(Instant timestamp, Integer status, String errorName, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.errorName = errorName;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
