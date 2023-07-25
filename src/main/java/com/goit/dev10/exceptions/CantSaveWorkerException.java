package com.goit.dev10.exceptions;

public class CantSaveWorkerException extends RuntimeException{
    public CantSaveWorkerException(Exception cause) {
        super(cause);
    }
}
