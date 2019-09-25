package com.juli.service;

import com.juli.entities.DiffResponse;
import com.juli.entities.Request;
import com.juli.enums.DiffResult;
import com.juli.enums.Side;
import com.juli.exceptions.Base64ValidationFailedException;
import com.juli.exceptions.DocumentNotFoundException;
import com.juli.exceptions.InvalidInputDataException;
import com.juli.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;

@Service
/**
 * Main service
 */
public class CompareService implements JSONCompareService {

    /**
     * Constructor inject the repository
     */
    private final DocumentRepository repository;
    public CompareService(DocumentRepository repository) {
        this.repository = repository;
    }

    /**
     * The save method is used to store Base64 encoded data in the left or right side of a Document identified by the parameter Id
     * @param id Document id where to store encoded data
     * @param data  Base64 encoded data
     * @param side  The side of the document where to store the encoded data
     * @throws Base64ValidationFailedException thrown if the Base64 data is not a valid JSON
     * @throws InvalidInputDataException    thrown if input data is null (id, left or right side)
     */
    public void save(Long id, String data, Side side) throws Base64ValidationFailedException, InvalidInputDataException {
        // While saving a new Document check if id, data or side is null, else throw InvalidInputDataException
        if (id == null) throw new InvalidInputDataException("Invalid id");
        if (data == null || data.isEmpty()) throw new InvalidInputDataException("Invalid base64 data");
        if (side == null) throw new InvalidInputDataException("Side not defined");
        // If no exception occurs, retrieve the Document with the provided id
        // If there is any Document with the provided Id return a new Empty Document
        Request request = getDocumentById(id);
        // Validate the base64 string
        if (isValidBase64(data)) {
            // Set Id to the retrieved Document
            request.setId(id);
            // Set data to the retrieved Document, based on the side
            switch (side) {
                case LEFT: {
                    request.setLeft(data);
                    break;
                }
                case RIGHT: {
                    request.setRight(data);
                    break;
                }
            }
            // Save the document on the DB
            repository.save(request);
        }
        else {
            throw new Base64ValidationFailedException("Base64 Validation failed");
        }
    }

    /**
     * Check if Base64 encoded data is a valid JSON
     * @param data the data to be validated
     * @return whethere is a valid JSON or not
     */
    private boolean isValidBase64(String data) {
        boolean isValid = true;
        if (data.isEmpty()) {
            isValid = false;
        }
        try {
            // if IllegalArgumentException occurs it means it's not a valid Base64
            Base64.getDecoder().decode(data);
        } catch (IllegalArgumentException e) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Get the JSON data from the Base64 encoded data
     * @param base64 base64 encoded data
     * @return byte[] of the JSON decoded data
     */
    private byte[] getJsonFromBase64(String base64) {
        boolean isValid = true;
         byte[] json = null;
        if (base64.isEmpty()) {
            isValid = false;
        }
        try {
            // if IllegalArgumentException occurs it means it's not a valid Base64
            json = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            isValid = false;
        }
        return json;
    }

    /**
     * Compare byte[] of right and left side of the Document with provided id
     * @param id Document id used to compare both sides
     * @return DiffResponse
     * @throws DocumentNotFoundException thrown when no Document is found with the provided Id
     * @throws Base64ValidationFailedException thrown if the Base64 data (left or right side) is not a valid JSON
     * @throws InvalidInputDataException  thrown if input data is null (id, left or right side)
     */
    public DiffResponse diffBase64(Long id) throws DocumentNotFoundException, InvalidInputDataException, Base64ValidationFailedException {
        // main method for the comparison of two base64 strings

        // final List<String> offsets = new ArrayList<>();
        // Retrieve the document with the provided id or if not found throw DocumentNotFoundException
        Request request = getDocumentById(id);
        DiffResponse diffResponse = new DiffResponse();
        if (request.getId() == 0L) throw new DocumentNotFoundException("Document not found :: id " + id);
        // If left or right is missing for the retrieved document throw InvalidInputDataException
        if (request.getLeft() == null || request.getRight() == null) {
            throw new InvalidInputDataException("Base64 data left or right is missing");
        }
        if(isValidBase64(request.getLeft()) && isValidBase64(request.getRight())) {
            byte[] bytesLeft = getJsonFromBase64(request.getLeft());
            byte[] bytesRight = getJsonFromBase64(request.getRight());
            // get array of bytes for the left and right side
            boolean byteArraysAreEqual = Arrays.equals(bytesLeft, bytesRight);

            if (byteArraysAreEqual) {
                // If the left byte array is the same with the right byte array => left base64 = right base64
                diffResponse.setMessage("Base64 data are equal");
                diffResponse.setResult(DiffResult.EQUAL);
            }
            else if (bytesLeft.length != bytesRight.length) {
                // If left byte array length != right byte array => the two base64 data have not the same size
                diffResponse.setMessage("Base64 data have not same size");
                diffResponse.setResult(DiffResult.DIFFERENT_SIZE);
            }
            else {
                // Otherwise the two base64 data have the same size but different offset
            /* byte different;
            for (int index = 0; index < bytesLeft.length; index++) {
                different = (byte) (bytesLeft[index] ^ bytesRight[index]);
                if (different != 0) {
                    offsets.add(String.valueOf(different));
                }
            }
            */
                // if required we can return also the offsets array in the response
                diffResponse.setMessage("Base64 data got the same size but different offsets");// + offsets.toString());
                diffResponse.setResult(DiffResult.DIFFERENT_OFFSET);
            }
        }
        else if(!isValidBase64(request.getLeft())){
            throw new Base64ValidationFailedException("Left side is not a JSON Base64 data");
        }
        else if(!isValidBase64(request.getRight())){
            throw new Base64ValidationFailedException("Right side is not a JSON Base64 data");
        }
        return diffResponse;
    }

    /**
     * Get the document from the local DB
     * @param id document id to retrieve
     * @return the Document identified by param id
     */
    private Request getDocumentById(Long id) {
        // Get document with the provided id from the DB or if not found return Empty Document
        return repository.findById(id).orElse(Request.builder().build());
    }

}
