package fi.sutinse.pdfextractor.service;

import fi.sutinse.pdfextractor.dto.ExtractionMethod;
import fi.sutinse.pdfextractor.dto.PdfExtractionResponse;
import fi.sutinse.pdfextractor.model.DocumentType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PdfExtractionServiceTest {

    @Inject
    PdfExtractionService pdfExtractionService;

    @Test
    public void testEmptyPdfData() {
        byte[] emptyData = new byte[0];
        PdfExtractionResponse response = pdfExtractionService.extractText(emptyData, "test.pdf");
        
        assertFalse(response.success());
        assertNotNull(response.errorMessage());
        assertNull(response.extractedText());
        assertEquals(DocumentType.UNKNOWN, response.documentType());
    }

    @Test
    public void testInvalidPdfData() {
        byte[] invalidData = "This is not a PDF".getBytes();
        PdfExtractionResponse response = pdfExtractionService.extractText(invalidData, "test.pdf");
        
        assertFalse(response.success());
        assertNotNull(response.errorMessage());
        assertTrue(response.errorMessage().contains("Failed to process PDF"));
    }
}