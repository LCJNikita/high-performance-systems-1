package ru.itmo.hpsproject.exeptions;

public class AlreadyExistsException extends Exception {

    public AlreadyExistsException(String message) {
        super(message);
    }
}
