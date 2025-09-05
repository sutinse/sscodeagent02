package fi.sutinse.pdfextractor.dto;

/** Record representing text location coordinates (for OCR results) */
public record TextLocation(
    int x, 
    int y, 
    int width, 
    int height,
    int pageNumber) {
  
  /** Factory method for creating text location */
  public static TextLocation of(int x, int y, int width, int height, int pageNumber) {
    return new TextLocation(x, y, width, height, pageNumber);
  }
}