package com.sling.hotel.infrastructure.adapter.in.rest;

public class InvalidSearchException extends RuntimeException {

    public InvalidSearchException(String message) {
        super(message);
    }
}
