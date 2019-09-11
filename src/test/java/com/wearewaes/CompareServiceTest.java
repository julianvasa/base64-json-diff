package com.wearewaes;

import com.wearewaes.entities.DiffResponse;
import com.wearewaes.entities.Request;
import com.wearewaes.enums.DiffResult;
import com.wearewaes.enums.Side;
import com.wearewaes.exceptions.Base64ValidationFailedException;
import com.wearewaes.exceptions.DocumentNotFoundException;
import com.wearewaes.exceptions.InvalidInputDataException;
import com.wearewaes.repository.DocumentRepository;
import com.wearewaes.service.CompareService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CompareServiceTest {

    private CompareService service;
    private DocumentRepository repository;
    private String sampleBase64;
    private Long sampleId;

    @Before
    public void init() {
        repository = mock(DocumentRepository.class);
        service = new CompareService(repository);
        sampleBase64 = "SGVsbG8gV0FFUw==";
        sampleId = 1L;
    }

    @Test(expected = InvalidInputDataException.class)
    public void shouldRejectWhenDocumentIdIsNull() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(null, null, null);
    }

    @Test(expected = InvalidInputDataException.class)
    public void shouldRejectWhenBase64StringIsNull() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, null, null);
    }

    @Test(expected = InvalidInputDataException.class)
    public void shouldRejectWhenBase64StringIsEmpty() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, "", null);
    }

    @Test(expected = InvalidInputDataException.class)
    public void shouldRejectWhenDiffSideIsNotSpecified() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, sampleBase64, null);
    }

    @Test
    public void shouldCreateNewDocumentWithLeftSide() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, sampleBase64, Side.LEFT);
        verify(repository).findById(eq(sampleId));
    }

    @Test
    public void shouldCreateNewDocumentWithLeftSideAndRightIsNull() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, sampleBase64, Side.LEFT);
        verify(repository).save(ArgumentMatchers.argThat(a -> {
            assertNull(a.getRight());
            return true;
        }));
    }

    @Test
    public void shouldCreateNewDocumentWithLeftSideAndRightIsNullAndLeftIsNotNull() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, sampleBase64, Side.LEFT);
        verify(repository).save(ArgumentMatchers.argThat(a -> {
            assertEquals(sampleBase64, a.getLeft());
            return true;
        }));
    }

    @Test
    public void shouldCreateNewDocumentWithRightSide() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, sampleBase64, Side.RIGHT);
        verify(repository).findById(eq(sampleId));
    }

    @Test
    public void shouldCreateNewDocumentWithRightSideAndRightIsNotNull() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, sampleBase64, Side.RIGHT);
        verify(repository).save(ArgumentMatchers.argThat(a -> {
            assertNull(a.getLeft());
            return true;
        }));
    }

    @Test
    public void shouldCreateNewDocumentWithRightSideAndRightIsNotNullAndLeftIsNull() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, sampleBase64, Side.RIGHT);
        verify(repository).save(ArgumentMatchers.argThat(a -> {
            assertEquals(sampleBase64, a.getRight());
            return true;
        }));
    }


    @Test(expected = Base64ValidationFailedException.class)
    public void shouldNotAcceptContentWithInvalidBase64() throws InvalidInputDataException, Base64ValidationFailedException {
        service.save(sampleId, "%4asddafgx!", Side.LEFT);
    }

    @Test(expected = DocumentNotFoundException.class)
    public void shouldFailWhenDocumentIdDoesNotExist() throws DocumentNotFoundException, InvalidInputDataException {
        service.diffBase64(sampleId);
    }

    @Test
    public void shouldUpdateLeftSideWhenDocumentIdExistsAndTheSideIsAlreadyDefined() throws Base64ValidationFailedException, InvalidInputDataException {
        when(repository.findById(eq(sampleId))).thenReturn(Optional.of(
            Request.builder()
                .id(sampleId)
                .left("aaaaa")
                .right("bbbbb")
                .build()));
        service.save(sampleId, sampleBase64, Side.LEFT);
        verify(repository).save(ArgumentMatchers.argThat(a -> {
            assertEquals(sampleBase64, a.getLeft());
            return true;
        }));
    }

    @Test
    public void shouldUpdateRightSideWhenDocumentIdExistsAndTheSideIsAlreadyDefined() throws Base64ValidationFailedException, InvalidInputDataException {
        when(repository.findById(eq(sampleId))).thenReturn(Optional.of(
            Request.builder()
                .id(sampleId)
                .left("aaaaa")
                .right("bbbbb")
                .build()));
        service.save(sampleId, sampleBase64, Side.RIGHT);
        verify(repository).save(ArgumentMatchers.argThat(a -> {
            assertEquals(sampleBase64, a.getRight());
            return true;
        }));
    }


    @Test
    public void shouldAddLeftSideAndKeepTheRightSideData() throws Base64ValidationFailedException, InvalidInputDataException {
        when(repository.findById(eq(sampleId))).thenReturn(Optional.of(
            Request.builder()
                .id(sampleId)
                .right(sampleBase64)
                .build()));

        Request expected = Request.builder()
            .id(sampleId)
            .left(sampleBase64)
            .right(sampleBase64)
            .build();

        service.save(sampleId, sampleBase64, Side.LEFT);

        verify(repository).findById(eq(sampleId));
        verify(repository).save(ArgumentMatchers.argThat(a -> {
            assertEquals(expected, a);
            return true;
        }));
    }

    @Test
    public void shouldAddRightSideAndKeepLeftSideData() throws Base64ValidationFailedException, InvalidInputDataException {
        when(repository.findById(eq(sampleId))).thenReturn(Optional.of(
            Request.builder()
                .id(sampleId)
                .left(sampleBase64)
                .build()));

        Request expected = Request.builder()
            .id(sampleId)
            .left(sampleBase64)
            .right(sampleBase64)
            .build();

        service.save(sampleId, sampleBase64, Side.RIGHT);

        verify(repository).findById(eq(sampleId));
        verify(repository).save(ArgumentMatchers.argThat(a -> {
            assertEquals(expected, a);
            return true;
        }));
    }

    @Test(expected = InvalidInputDataException.class)
    public void shouldFailWhenBothSidesAreMissing() throws DocumentNotFoundException, InvalidInputDataException {
        when(repository.findById(sampleId))
            .thenReturn(Optional.of(Request.builder()
                .id(sampleId)
                .build()));

        service.diffBase64(sampleId);
    }

    @Test(expected = InvalidInputDataException.class)
    public void shouldFailWhenLeftSideIsMissing() throws DocumentNotFoundException, InvalidInputDataException {
        when(repository.findById(sampleId))
            .thenReturn(Optional.of(Request.builder()
                .id(sampleId)
                .right(sampleBase64)
                .build()));

        service.diffBase64(sampleId);
    }

    @Test(expected = InvalidInputDataException.class)
    public void shouldFailWhenRightSideIsMissing() throws DocumentNotFoundException, InvalidInputDataException {
        when(repository.findById(sampleId))
            .thenReturn(Optional.of(Request.builder()
                .id(sampleId)
                .left(sampleBase64)
                .build()));

        service.diffBase64(sampleId);
    }

    @Test
    public void shouldReturnEqualwhenBothSidesAreEqual() throws DocumentNotFoundException, InvalidInputDataException {
        when(repository.findById(sampleId))
            .thenReturn(Optional.of(Request.builder().id(sampleId).left(sampleBase64).right(sampleBase64).build()));
        assertEquals(DiffResult.EQUAL, service.diffBase64(sampleId).getResult());
    }

    @Test
    public void shouldReturnDifferentSize() throws DocumentNotFoundException, InvalidInputDataException {
        when(repository.findById(sampleId))
            .thenReturn(Optional.of(Request.builder().id(sampleId).left("a").right("aa").build()));

        for (int i = 0; i < 4; i++) {
            assertEquals(DiffResult.DIFFERENT_SIZE, service.diffBase64(sampleId).getResult());
        }
    }

    @Test
    public void shouldReturnDifferentOffset() throws DocumentNotFoundException, InvalidInputDataException {
        when(repository.findById(sampleId))
            .thenReturn(Optional.of(Request.builder().id(sampleId).left("bXlkb21haW4uY29tOmRueURucz").right("bXlkb21haW4uY29tOmR5bkRucz").build()));

        final DiffResponse response = service.diffBase64(sampleId);
        assertEquals(DiffResult.DIFFERENT_OFFSET, response.getResult());
    }

    @Test
    public void shouldReturnDifferentOffsetAndReturnDiffOffsetArray() throws DocumentNotFoundException, InvalidInputDataException {
        when(repository.findById(sampleId))
            .thenReturn(Optional.of(Request.builder().id(sampleId).left("bXlkb21haW4uY29tOmRueURucz").right("bXlkb21haW4uY29tOmR5bkRucz").build()));

        final DiffResponse response = service.diffBase64(sampleId);
        assertEquals("Base64 data got the same size but different offsets", response.getMessage());
    }
}
