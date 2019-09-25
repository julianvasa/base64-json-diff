package com.juli.entities;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
/**
 * A Response is the object that is returned on successful insert (post request)
 */
public class Response {
    private String message;
    private HttpStatus httpStatus;
}
