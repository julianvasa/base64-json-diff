package com.wearewaes.service;

import com.wearewaes.entities.DiffResponse;
import com.wearewaes.entities.Request;
import com.wearewaes.enums.DiffResult;
import com.wearewaes.enums.Side;
import com.wearewaes.exceptions.Base64ValidationFailedException;
import com.wearewaes.exceptions.DocumentNotFoundException;
import com.wearewaes.exceptions.InvalidInputDataException;
import com.wearewaes.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;

@Service
public class CompareService {

    private final DocumentRepository repository;
    // Constructor inject the repository
    public CompareService(DocumentRepository repository) {
        this.repository = repository;
    }

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

    public DiffResponse diffBase64(Long id) throws DocumentNotFoundException, InvalidInputDataException {
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
        byte[] bytesLeft = request.getLeft().getBytes();
        byte[] bytesRight = request.getRight().getBytes();
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
        return diffResponse;
    }

    private Request getDocumentById(Long id) {
        // Get document with the provided id from the DB or if not found return Empty Document
        return repository.findById(id).orElse(Request.builder().build());
    }

}
