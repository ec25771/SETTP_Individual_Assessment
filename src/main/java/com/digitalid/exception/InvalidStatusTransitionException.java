package com.digitalid.exception;

import com.digitalid.model.IdentityStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(IdentityStatus current, IdentityStatus target) {
        super("Cannot transition from " + current + " to " + target);
    }
}
