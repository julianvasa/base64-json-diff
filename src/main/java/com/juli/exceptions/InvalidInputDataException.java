package com.juli.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
/**
 * This exception is thrown when input data is null (id, left or right)
 */
public class InvalidInputDataException extends Exception{
    public InvalidInputDataException(String message){
        super(message);
    }
}
