package com.backend.exeptions;

public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException() {
        super("Username already in use");
    }
}
