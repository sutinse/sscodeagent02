package fi.sutinse.pdfextractor.service;

import fi.sutinse.pdfextractor.dto.ExtractionMetadata;
import fi.sutinse.pdfextractor.dto.ExtractionMethod;
import fi.sutinse.pdfextractor.dto.PdfExtractionResponse;
import fi.sutinse.pdfextractor.dto.StructuredText;
import fi.sutinse.pdfextractor.model.DocumentType;
import fi.sutinse.pdfextractor.model.Language;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for extracting text from PDF files using PDFBox and TesseractOCR */
@ApplicationScoped
public class PdfExtractionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PdfExtractionService.class);

  @Inject TesseractOcrService tesseractService;

  @Inject TextNormalizationService normalizationService;

  /**
   * Extracts text from PDF using PDFBox first, then TesseractOCR as fallback
   *
   * @param pdfData PDF file data as byte array
   * @param filename Original filename
   * @return Extraction response with text and metadata
   */
  public PdfExtractionResponse extractText(byte[] pdfData, String filename) {
    long startTime = System.currentTimeMillis();

    try {
      // First try PDFBox extraction
      LOGGER.info("Attempting PDFBox extraction for file: {}", filename);

      try (PDDocument document = Loader.loadPDF(pdfData)) {
        PDFTextStripper textStripper = new PDFTextStripper();
        String extractedText = textStripper.getText(document);

        // Check if PDFBox extracted meaningful text
        if (isTextMeaningful(extractedText)) {
          long processingTime = System.currentTimeMillis() - startTime;

          // Detect language and document type
          Language detectedLanguage = Language.detectFromContent(extractedText);
          DocumentType docType = DocumentType.detectFromContent(extractedText, detectedLanguage);

          ExtractionMetadata metadata =
              ExtractionMetadata.create(
                  filename,
                  pdfData.length,
                  document.getNumberOfPages(),
                  processingTime,
                  false,
                  detectedLanguage.getTesseractCode());

          LOGGER.info(
              "PDFBox extraction successful for file: {}, detected type: {}, language: {}",
              filename,
              docType,
              detectedLanguage.getEnglishName());

          return PdfExtractionResponse.success(
              StructuredText.fromText(extractedText.trim()),
              ExtractionMethod.PDFBOX,
              docType,
              metadata);
        }
      }

      // If PDFBox fails or returns insufficient text, try TesseractOCR
      LOGGER.info("PDFBox extraction insufficient, attempting TesseractOCR for file: {}", filename);
      return extractWithOcr(pdfData, filename, startTime);

    } catch (IOException e) {
      LOGGER.error("Error during PDF extraction for file: {}", filename, e);
      return PdfExtractionResponse.failure("Failed to process PDF: " + e.getMessage());
    }
  }

  private PdfExtractionResponse extractWithOcr(byte[] pdfData, String filename, long startTime) {
    try {
      // Extract structured text with OCR and location data
      StructuredText structuredText = tesseractService.extractStructuredTextFromPdf(pdfData);

      if (structuredText == null
          || structuredText.content() == null
          || structuredText.content().trim().isEmpty()) {
        return PdfExtractionResponse.failure("No text could be extracted using OCR");
      }

      // Detect language from OCR text
      Language detectedLanguage = Language.detectFromContent(structuredText.content());

      // Normalize text using detected language
      String normalizedText =
          normalizationService.normalizeText(structuredText.content(), detectedLanguage);
      DocumentType docType = DocumentType.detectFromContent(normalizedText, detectedLanguage);

      long processingTime = System.currentTimeMillis() - startTime;

      // Get page count using PDFBox (for metadata)
      int pageCount = 1;
      try (PDDocument document = Loader.loadPDF(pdfData)) {
        pageCount = document.getNumberOfPages();
      } catch (IOException e) {
        LOGGER.warn("Could not determine page count for file: {}", filename);
      }

      ExtractionMetadata metadata =
          ExtractionMetadata.create(
              filename,
              pdfData.length,
              pageCount,
              processingTime,
              true,
              detectedLanguage.getTesseractCode());

      LOGGER.info(
          "TesseractOCR extraction successful for file: {}, detected type: {}, language: {}",
          filename,
          docType,
          detectedLanguage.getEnglishName());

      // Update the structured text content with normalized text
      StructuredText finalStructuredText =
          StructuredText.fromElements(normalizedText, structuredText.elements());

      return PdfExtractionResponse.success(
          finalStructuredText, ExtractionMethod.TESSERACT_OCR, docType, metadata);

    } catch (Exception e) {
      LOGGER.error("Error during OCR extraction for file: {}", filename, e);
      return PdfExtractionResponse.failure("OCR extraction failed: " + e.getMessage());
    }
  }

  /** Checks if extracted text is meaningful (not just whitespace or garbage) */
  private boolean isTextMeaningful(String text) {
    if (text == null || text.trim().isEmpty()) {
      return false;
    }

    String cleaned = text.replaceAll("\\s+", " ").trim();

    // Text should have at least 10 characters and contain some letters
    return cleaned.length() >= 10 && cleaned.chars().anyMatch(Character::isLetter);
  }
}
