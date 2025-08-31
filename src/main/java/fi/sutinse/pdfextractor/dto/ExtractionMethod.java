package fi.sutinse.pdfextractor.dto;

/**
 * Enum representing the extraction method used
 */
public enum ExtractionMethod {
    PDFBOX("PDFBox - Direct text extraction"),
    TESSERACT_OCR("TesseractOCR - Optical Character Recognition"),
    HYBRID("Hybrid - PDFBox with OCR fallback");

    private final String description;

    ExtractionMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}