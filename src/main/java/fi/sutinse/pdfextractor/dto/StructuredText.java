package fi.sutinse.pdfextractor.dto;

import java.util.List;

/** Record representing structured extracted text in JSON format */
public record StructuredText(
    String content,
    List<TextElement> elements,
    boolean hasLocationData) {
  
  /** Factory method for simple text without location data (PDFBox) */
  public static StructuredText fromText(String text) {
    return new StructuredText(
        text,
        List.of(TextElement.textOnly(text)), 
        false
    );
  }
  
  /** Factory method for text with location data (OCR) */
  public static StructuredText fromElements(String fullContent, List<TextElement> elements) {
    return new StructuredText(
        fullContent,
        elements,
        true
    );
  }
}