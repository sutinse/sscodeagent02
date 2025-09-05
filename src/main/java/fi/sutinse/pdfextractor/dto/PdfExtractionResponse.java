package fi.sutinse.pdfextractor.dto;

import fi.sutinse.pdfextractor.model.DocumentType;

/** Record for PDF extraction response using modern Java record syntax */
public record PdfExtractionResponse(
    StructuredText extractedText,
    ExtractionMethod methodUsed,
    DocumentType documentType,
    boolean success,
    String errorMessage,
    ExtractionMetadata metadata) {

  /** Factory method for successful extraction */
  public static PdfExtractionResponse success(
      StructuredText text, ExtractionMethod method, DocumentType docType, ExtractionMetadata metadata) {
    return new PdfExtractionResponse(text, method, docType, true, null, metadata);
  }

  /** Factory method for failed extraction */
  public static PdfExtractionResponse failure(String errorMessage) {
    return new PdfExtractionResponse(null, null, DocumentType.UNKNOWN, false, errorMessage, null);
  }
}
