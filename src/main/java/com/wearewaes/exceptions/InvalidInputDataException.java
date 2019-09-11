package com.wearewaes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidInputDataException extends Exception{

    public InvalidInputDataException(String message){
        super(message);
    }
}