package fi.sutinse.pdfextractor.service;

import jakarta.enterprise.context.ApplicationScoped;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Service for OCR text extraction using TesseractOCR
 */
@ApplicationScoped
public class TesseractOcrService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TesseractOcrService.class);
    
    private final Tesseract tesseract;

    public TesseractOcrService() {
        this.tesseract = new Tesseract();
        configureTesseract();
    }

    private void configureTesseract() {
        try {
            // Set Finnish language for better recognition
            tesseract.setLanguage("fin");
            
            // Configure OCR engine mode and page segmentation
            tesseract.setOcrEngineMode(1); // Neural nets LSTM engine
            tesseract.setPageSegMode(1);   // Automatic page segmentation with OSD
            
            // Additional configuration for better Finnish text recognition
            tesseract.setVariable("preserve_interword_spaces", "1");
            tesseract.setVariable("user_defined_dpi", "300");
            
            LOGGER.info("TesseractOCR configured for Finnish language");
            
        } catch (Exception e) {
            LOGGER.warn("Could not configure TesseractOCR for Finnish, using default settings: {}", 
                       e.getMessage());
        }
    }

    /**
     * Extracts text from PDF using OCR
     *
     * @param pdfData PDF file data as byte array
     * @return Extracted text
     * @throws TesseractException if OCR fails
     * @throws IOException if PDF processing fails
     */
    public String extractTextFromPdf(byte[] pdfData) throws TesseractException, IOException {
        LOGGER.debug("Starting OCR extraction for PDF data of size: {} bytes", pdfData.length);
        
        StringBuilder extractedText = new StringBuilder();
        
        try (PDDocument document = Loader.loadPDF(pdfData)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();
            
            LOGGER.debug("Processing {} pages with OCR", pageCount);
            
            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                try {
                    // Render PDF page as image at high DPI for better OCR
                    BufferedImage image = renderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);
                    
                    // Extract text from image using Tesseract
                    String pageText = tesseract.doOCR(image);
                    
                    if (pageText != null && !pageText.trim().isEmpty()) {
                        extractedText.append(pageText);
                        if (pageIndex < pageCount - 1) {
                            extractedText.append("\n\n--- Page ").append(pageIndex + 2).append(" ---\n\n");
                        }
                    }
                    
                    LOGGER.debug("OCR completed for page {}/{}", pageIndex + 1, pageCount);
                    
                } catch (TesseractException e) {
                    LOGGER.warn("OCR failed for page {}: {}", pageIndex + 1, e.getMessage());
                    // Continue with other pages
                }
            }
        }
        
        String result = extractedText.toString().trim();
        LOGGER.info("OCR extraction completed, extracted {} characters", result.length());
        
        return result;
    }

    /**
     * Extracts text from a single image
     *
     * @param image BufferedImage to process
     * @return Extracted text
     * @throws TesseractException if OCR fails
     */
    public String extractTextFromImage(BufferedImage image) throws TesseractException {
        return tesseract.doOCR(image);
    }
}