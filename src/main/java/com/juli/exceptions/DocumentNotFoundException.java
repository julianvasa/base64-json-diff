package com.juli.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
/**
 * The requested document has not been found in the local DB
 */
public class DocumentNotFoundException extends Exception{

    public DocumentNotFoundException(String message){
        super(message);
    }
}
