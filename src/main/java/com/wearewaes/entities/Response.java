package com.wearewaes.entities;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Response {
    /**
     * A Response is the object that is returned on successful insert (post request)
     */
    private String message;
    private HttpStatus httpStatus;
}
