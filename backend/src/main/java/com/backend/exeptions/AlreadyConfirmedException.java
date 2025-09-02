package com.backend.exeptions;

public class AlreadyConfirmedException extends RuntimeException {
    public AlreadyConfirmedException() {
        super("Account is already confirmed");
    }

    public AlreadyConfirmedException(String message) {
        super(message);
    }
}
