package com.wearewaes.entities;

import com.wearewaes.enums.DiffResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiffResponse {
    /**
     * A DiffResponse is the object that is returned on a get request to compare two base64 data
     */
    private String message;
    private DiffResult result;
}
