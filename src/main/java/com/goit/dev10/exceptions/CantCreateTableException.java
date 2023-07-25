package com.goit.dev10.exceptions;

public class CantCreateTableException extends RuntimeException{
    public CantCreateTableException(String message) {
        super(message);
    }
}
