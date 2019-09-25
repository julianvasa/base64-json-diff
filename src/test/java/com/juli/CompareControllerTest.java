package com.juli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juli.entities.DiffResponse;
import com.juli.entities.Request;
import com.juli.entities.Response;
import com.juli.enums.DiffResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class CompareControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String sampleBase64;
    private Long sampleId;
    private Request sampleRequest;
    private DiffResponse equalDocuments;
    private DiffResponse differentSizeDocuments;
    private DiffResponse differentOffsetDocuments;
    private MvcResult resultWhenSavingLeftSide;
    private MvcResult resultWhenSavingRightSide;
    private String invalidSampleBase64;

    @Before
    public void init() {
        sampleBase64 = "SGVsbG8gV0FFUw==";
        sampleId = 1L;
        equalDocuments = new DiffResponse("Base64 data are equal", DiffResult.EQUAL);
        differentSizeDocuments = new DiffResponse("Base64 data have not same size", DiffResult.DIFFERENT_SIZE);
        differentOffsetDocuments = new DiffResponse("Base64 data got the same size but different offsets", DiffResult.DIFFERENT_OFFSET);
        invalidSampleBase64 = "%4asddafgx!";
    }

    @Test
    public void shouldAddLeftSideAndDocumentIsCreated() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/left", sampleId)
            .content(sampleBase64)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isCreated()).andReturn();
        String response = resultWhenSavingLeftSide.getResponse().getContentAsString();
        Response jsonResponse = mapper.readValue(response, Response.class);
        assertEquals("Document with id " + sampleId + ", side LEFT has been successfully stored in the DB!", jsonResponse.getMessage());
    }

    @Test
    public void whenLeftSideIsEmptyThenDocumentIsNotCreated() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/left", sampleId)
            .content("")
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void whenRightSideIsEmptyThenDocumentIsNotCreated() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/right", sampleId)
            .content("")
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void whenSideIsNotSpecifiedThenThrowError405() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/", sampleId)
            .content("")
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isMethodNotAllowed()).andReturn();
    }

    @Test
    public void whenPostingRightSideAndIdIsEmptyThenThrowError405() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/right", "")
            .content(sampleBase64)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isMethodNotAllowed()).andReturn();
    }

    @Test
    public void whenPostingLeftSideAndIdIsEmptyThenThrowError405() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/left", "")
            .content(sampleBase64)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isMethodNotAllowed()).andReturn();
    }

    @Test
    public void shouldAddRightSideAndDocumentIsCreated() throws Exception {
        resultWhenSavingRightSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/right", sampleId)
            .content(sampleBase64)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isCreated()).andReturn();
        String response = resultWhenSavingRightSide.getResponse().getContentAsString();
        Response jsonResponse = mapper.readValue(response, Response.class);
        assertEquals("Document with id " + sampleId + ", side RIGHT has been successfully stored in the DB!", jsonResponse.getMessage());
    }


    @Test
    public void shouldAddBothSidesAndGetEquals() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/left", sampleId)
            .content(sampleBase64)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isCreated()).andReturn();

        resultWhenSavingRightSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/right", sampleId)
            .content(sampleBase64)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isCreated()).andReturn();

        MvcResult diffResult = mvc.perform(MockMvcRequestBuilders
            .get("/v1/diff/{id}", sampleId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();

        String DiffResponse = diffResult.getResponse().getContentAsString();
        DiffResponse jsonResponse = mapper.readValue(DiffResponse, DiffResponse.class);
        assertEquals(jsonResponse, equalDocuments);
    }

    @Test
    public void shouldAddRightSideWithInvalidBase64DataAndDocumentIsNotCreated() throws Exception {
        resultWhenSavingRightSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/right", sampleId)
            .content(invalidSampleBase64)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isBadRequest()).andReturn();
        String response = resultWhenSavingRightSide.getResponse().getContentAsString();
        Response jsonResponse = mapper.readValue(response, Response.class);
        assertEquals("Base64 Validation failed", jsonResponse.getMessage());
    }


    @Test
    public void shouldAddLeftSideWithInvalidBase64DataAndDocumentIsNotCreated() throws Exception {
        resultWhenSavingRightSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/left", sampleId)
            .content(invalidSampleBase64)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isBadRequest()).andReturn();
        String response = resultWhenSavingRightSide.getResponse().getContentAsString();
        Response jsonResponse = mapper.readValue(response, Response.class);
        assertEquals("Base64 Validation failed", jsonResponse.getMessage());
    }

    @Test
    public void shouldFailWhenDocumentIdDoesNotExist() throws Exception {
        mvc.perform(MockMvcRequestBuilders
            .get("/v1/diff/{id}", sampleId)
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andReturn();
    }

    @Test
    public void shouldReturnDifferentSize() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/left", sampleId)
            .content("ewogIndhZXMiIDogInllcyIKfQ==")
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isCreated()).andReturn();

        resultWhenSavingRightSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/right", sampleId)
            .content("ewogIndhZXMiIDogIk5vIgp9")
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isCreated()).andReturn();

        MvcResult diffResult = mvc.perform(MockMvcRequestBuilders
            .get("/v1/diff/{id}", sampleId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();

        String DiffResponse = diffResult.getResponse().getContentAsString();
        DiffResponse jsonResponse = mapper.readValue(DiffResponse, DiffResponse.class);
        assertEquals(jsonResponse, differentSizeDocuments);
    }

    @Test
    public void shouldReturnDifferentOffset() throws Exception {
        resultWhenSavingLeftSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/left", sampleId)
            .content("bXlkb21haW4uY29tOmR5bkRucz")
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isCreated()).andReturn();

        resultWhenSavingRightSide = mvc.perform(MockMvcRequestBuilders
            .post("/v1/diff/{id}/right", sampleId)
            .content("bXlkb21haW4uY29tOmRueURucz")
            .contentType(MediaType.TEXT_PLAIN)
            .accept(MediaType.ALL))
            .andExpect(status().isCreated()).andReturn();

        MvcResult diffResult = mvc.perform(MockMvcRequestBuilders
            .get("/v1/diff/{id}", sampleId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andReturn();

        String DiffResponse = diffResult.getResponse().getContentAsString();
        DiffResponse jsonResponse = mapper.readValue(DiffResponse, DiffResponse.class);
        assertEquals(jsonResponse, differentOffsetDocuments);
    }


}
