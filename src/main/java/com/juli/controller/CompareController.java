package com.juli.controller;

import com.google.gson.Gson;
import com.juli.entities.Response;
import com.juli.enums.Side;
import com.juli.exceptions.Base64ValidationFailedException;
import com.juli.exceptions.DocumentNotFoundException;
import com.juli.exceptions.InvalidInputDataException;
import com.juli.service.JSONCompareService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/diff/{id}")
@Api(value = "Store and compare json base64 encoded data")
/**
 * Main rest controller
 * Store and compare json base64 encoded data
 */
public class CompareController {

    @Autowired
    private JSONCompareService service;
    private final Gson gson = new Gson();

    @RequestMapping(value = "/left", method = RequestMethod.POST, consumes = "text/plain")
    @ApiOperation(value = "Store Base64 encoded data on the left of the document")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Successfully store the encoded data on the left of the document"),
        @ApiResponse(code = 400, message = "Base64 JSON validation failed or input data provided are null"),
       })
    /**
     * Store base64 data on the left side of the Document with id = @Pathvariable Long id
     * @param id Document id where to store left side (json base64 encoded)
     */
    private ResponseEntity<Response> left(
        @ApiParam(value = "Document id where to store the encoded data", required = true) @PathVariable Long id,
        @ApiParam(value = "Base64 Encoded data to be store on the left side of the document", required = true) @RequestBody String data) {
        return storeData(data, id, Side.LEFT);
    }

    @RequestMapping(value = "/right", method = RequestMethod.POST, consumes = "text/plain")
    @ApiOperation(value = "Store Base64 encoded data on the right of the document")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Successfully store the encoded data on the right of the document"),
        @ApiResponse(code = 400, message = "Base64 JSON validation failed or input data provided are null"),
    })
    /**
     * Store base64 data on the right side of the Document with id = @Pathvariable Long id
     * @param id Document id where to store left side (json base64 encoded)
     */
    private ResponseEntity<Response> right(@ApiParam(value = "Document id where to store the encoded data", required = true) @PathVariable Long id,
        @ApiParam(value = "Base64 Encoded data to be store on the right side of the document", required = true) @RequestBody String data) {
        return storeData(data, id, Side.RIGHT);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Compare both sides of a document by decoding, getting the JSON and comparing their byte array")
    /**
     * Return the json object with the response of the comparison of the two side of the Document with id = @Pathvariable Long id
     * @param id Document id to compare both sides (left and right)
     */
    private String difference(@ApiParam(value = "Document id where to store the encoded data", required = true) @PathVariable Long id) throws DocumentNotFoundException, InvalidInputDataException, Base64ValidationFailedException {
        return (gson.toJson(service.diffBase64(id)));
    }

    /**
     * Used by post endpoints to save data to the local storage
     *
     * @param data data to be stored (base64 encoded)
     * @param id   document id where to store the encoded data
     * @param side document side where to store the data
     * @return message and httpStatus
     */
    private ResponseEntity<Response> storeData(String data, Long id, Side side) {
        // If no exception occurs HttpStatus.CREATED is return with a message
        HttpStatus httpStatus = HttpStatus.CREATED;
        String message = "Document with id " + id + ", side " + side + " has been successfully stored in the DB!";

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

