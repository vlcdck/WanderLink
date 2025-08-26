package com.backend.exeptions;

public class AccountNotConfirmedException extends RuntimeException {
    public AccountNotConfirmedException() { super("Account not confirmed"); }
}
