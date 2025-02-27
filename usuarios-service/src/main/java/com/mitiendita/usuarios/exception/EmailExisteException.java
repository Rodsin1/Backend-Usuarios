package com.mitiendita.usuarios.exception;

public class EmailExisteException extends RuntimeException {

    public EmailExisteException(String message) {
        super(message);
    }
}