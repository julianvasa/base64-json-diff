package com.juli.entities;

import com.juli.enums.DiffResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * A DiffResponse is the object that is returned on a get request to compare two base64 data
 */
public class DiffResponse {
    private String message;
    private DiffResult result;
}
