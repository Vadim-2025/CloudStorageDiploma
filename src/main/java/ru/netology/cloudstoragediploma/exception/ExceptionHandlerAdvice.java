package ru.netology.cloudstoragediploma.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.cloudstoragediploma.model.ErrorMessage;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage fileNotFoundHandler(FileNotFoundException e) {
        log.error("Файл не найден: {}", e.getMessage());
        return new ErrorMessage(e.getMessage(), e.getId());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage userNotFoundHandler(UserNotFoundException e) {
        log.error("Пользователь не найден: {}", e.getMessage());
        return new ErrorMessage(e.getMessage(), e.getId());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage userAlreadyCreatedHandler(UserAlreadyExistsException e) {
        log.error("Пользователь уже существует: {}", e.getMessage());
        return new ErrorMessage(e.getMessage(), e.getId());
    }

    @ExceptionHandler(InvalidInputDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage invalidInputDataHandler(InvalidInputDataException e) {
        log.error("Неверные входные данные: {}", e.getMessage());
        return new ErrorMessage(e.getMessage(), e.getId());
    }
}