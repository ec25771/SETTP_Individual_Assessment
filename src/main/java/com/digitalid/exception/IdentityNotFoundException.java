package com.digitalid.exception;

public class IdentityNotFoundException extends RuntimeException {

    public IdentityNotFoundException(String id) {
        super("Identity not found: " + id);
    }
}
