package fi.sutinse.pdfextractor.dto;

import java.util.List;
import java.util.Map;

/** Record representing structured extracted text in JSON format */
public record StructuredText(
    String content, 
    List<TextElement> elements, 
    boolean hasLocationData,
    Map<String, Object> structuredData) {

  /** Factory method for simple text without location data (PDFBox) */
  public static StructuredText fromText(String text) {
    return new StructuredText(text, List.of(TextElement.textOnly(text)), false, Map.of());
  }

  /** Factory method for simple text with structured data (PDFBox) */
  public static StructuredText fromText(String text, Map<String, Object> structuredData) {
    return new StructuredText(text, List.of(TextElement.textOnly(text)), false, structuredData);
  }

  /** Factory method for text with location data (OCR) */
  public static StructuredText fromElements(String fullContent, List<TextElement> elements) {
    return new StructuredText(fullContent, elements, true, Map.of());
  }

  /** Factory method for text with location data and structured data (OCR) */
  public static StructuredText fromElements(
      String fullContent, List<TextElement> elements, Map<String, Object> structuredData) {
    return new StructuredText(fullContent, elements, true, structuredData);
  }
}
