package com.wearewaes.controller;

import com.google.gson.Gson;
import com.wearewaes.entities.Response;
import com.wearewaes.enums.Side;
import com.wearewaes.exceptions.Base64ValidationFailedException;
import com.wearewaes.exceptions.DocumentNotFoundException;
import com.wearewaes.exceptions.InvalidInputDataException;
import com.wearewaes.service.CompareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/diff/{id}")
public class CompareController {

    @Autowired
    private CompareService service;
    private final Gson gson = new Gson();

    @RequestMapping(value = "/left", method = RequestMethod.POST, consumes = "text/plain")
    private ResponseEntity<Response> left(@PathVariable Long id, @RequestBody String data) {
        // Store base64 data on the Left side of the Document with id = @Pathvariable Long id
        return storeData(data, id, Side.LEFT);
    }

    @RequestMapping(value = "/right", method = RequestMethod.POST, consumes = "text/plain")
    private ResponseEntity<Response> right(@PathVariable Long id, @RequestBody String data) {
        // Store base64 data on the Right side of the Document with id = @Pathvariable Long id
        return storeData(data, id, Side.RIGHT);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    private String difference(@PathVariable Long id) throws DocumentNotFoundException, InvalidInputDataException {
        // Return the json object with the response of the comparison of the two side of the Document with id = @Pathvariable Long id
        return (gson.toJson(service.diffBase64(id)));
    }

    private ResponseEntity<Response> storeData(String data, Long id, Side side){
        // If no exception occurs HttpStatus.CREATED is return with a message
        HttpStatus httpStatus = HttpStatus.CREATED;
        String message = "Document with id " + id + ", side "+ side + " has been successfully stored in the DB!";

        try {
            service.save(id, data, side);
        } catch (Base64ValidationFailedException | InvalidInputDataException e) {
            // If Base64ValidationFailedException occurs or InvalidInputDataException return BAD_REQUEST and a message
            httpStatus = HttpStatus.BAD_REQUEST;
            message = e.getMessage();
        }

        // Return response JSON with the message and HttpStatus
        Response response = new Response();
        response.setMessage(message);
        response.setHttpStatus(httpStatus);
        return new ResponseEntity<>(response, httpStatus);
    }
}

