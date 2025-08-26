package com.backend.exeptions;

public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException() { super("Expired or revoked refresh token"); }
}
