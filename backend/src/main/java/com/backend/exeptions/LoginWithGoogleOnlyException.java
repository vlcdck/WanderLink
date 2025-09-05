package com.backend.exeptions;

public class LoginWithGoogleOnlyException extends RuntimeException {
    public LoginWithGoogleOnlyException(String email) {
        super("Акаунт " + email + " створений через Google. Встановіть пароль у налаштуваннях профілю.");
    }
}
