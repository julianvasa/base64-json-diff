package com.wearewaes.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DocumentNotFoundException extends Exception{

    public DocumentNotFoundException(String message){
        super(message);
    }
}