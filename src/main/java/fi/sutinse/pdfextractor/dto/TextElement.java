package fi.sutinse.pdfextractor.dto;

import java.util.List;

/** Record representing a text element with optional location information */
public record TextElement(
    String text,
    List<TextLocation> locations) {
  
  /** Factory method for text without location (PDFBox results) */
  public static TextElement textOnly(String text) {
    return new TextElement(text, null);
  }
  
  /** Factory method for text with locations (OCR results) */
  public static TextElement withLocations(String text, List<TextLocation> locations) {
    return new TextElement(text, locations);
  }
}