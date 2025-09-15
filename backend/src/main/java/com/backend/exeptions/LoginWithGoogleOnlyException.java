package com.backend.exeptions;

public class LoginWithGoogleOnlyException extends RuntimeException {
    public LoginWithGoogleOnlyException(String email) {
        super("Account " + email + " created by Google. Set up password at your account properties.");
    }
}
