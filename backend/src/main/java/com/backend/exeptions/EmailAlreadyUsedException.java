package com.backend.exeptions;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException() { super("Email already in use"); }
}
