package fi.sutinse.pdfextractor.dto;

import java.time.LocalDateTime;

/** Record for extraction metadata using modern Java record syntax */
public record ExtractionMetadata(
    LocalDateTime extractionTime,
    long processingTimeMs,
    int pageCount,
    String originalFilename,
    long fileSizeBytes,
    boolean textNormalized,
    String language) {

  /** Factory method for creating metadata */
  public static ExtractionMetadata create(
      String filename,
      long fileSize,
      int pages,
      long processingTime,
      boolean normalized,
      String lang) {
    return new ExtractionMetadata(
        LocalDateTime.now(), processingTime, pages, filename, fileSize, normalized, lang);
  }
}
