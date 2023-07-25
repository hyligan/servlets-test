package com.goit.dev10.exceptions;

public class CantGetConnectionFromPoolException extends RuntimeException {
    public CantGetConnectionFromPoolException(Exception e) {
        super(e);
    }
}
