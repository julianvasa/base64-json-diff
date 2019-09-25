package com.juli.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
/**
 * This exception is thrown when the provided Base64 is not a valid JSON data
 */
public class Base64ValidationFailedException extends Exception{

    public Base64ValidationFailedException(String message){
        super(message);
    }
}
