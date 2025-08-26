package com.backend.exeptions;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException() { super("Token expired"); }
}
