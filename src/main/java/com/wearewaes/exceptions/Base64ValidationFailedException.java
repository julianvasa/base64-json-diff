package com.wearewaes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class Base64ValidationFailedException extends Exception{

    public Base64ValidationFailedException(String message){
        super(message);
    }
}