package ru.netology.cloudstoragediploma.exception;

public class UserAlreadyExistsException extends RuntimeException {
    private final long id;

    public UserAlreadyExistsException(String msg, long id) {
        super(msg);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}