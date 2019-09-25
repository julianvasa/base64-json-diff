package com.juli.service;

import com.juli.entities.DiffResponse;
import com.juli.enums.Side;
import com.juli.exceptions.Base64ValidationFailedException;
import com.juli.exceptions.DocumentNotFoundException;
import com.juli.exceptions.InvalidInputDataException;

public interface JSONCompareService {
    void save(Long id, String data, Side side) throws Base64ValidationFailedException, InvalidInputDataException;

    DiffResponse diffBase64(Long id) throws DocumentNotFoundException, InvalidInputDataException, Base64ValidationFailedException;

}
