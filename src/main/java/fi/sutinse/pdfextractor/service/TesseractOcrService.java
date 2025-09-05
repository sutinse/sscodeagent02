package fi.sutinse.pdfextractor.service;

import fi.sutinse.pdfextractor.dto.StructuredText;
import fi.sutinse.pdfextractor.dto.TextElement;
import fi.sutinse.pdfextractor.dto.TextLocation;
import fi.sutinse.pdfextractor.model.Language;
import jakarta.enterprise.context.ApplicationScoped;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for OCR text extraction using TesseractOCR */
@ApplicationScoped
public class TesseractOcrService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TesseractOcrService.class);

  private final Tesseract tesseract;

  @ConfigProperty(name = "tesseract.language", defaultValue = "fin")
  String defaultLanguage;

  @ConfigProperty(name = "tesseract.auto-detect-language", defaultValue = "true")
  boolean autoDetectLanguage;

  public TesseractOcrService() {
    this.tesseract = new Tesseract();
    configureTesseract();
  }

  private void configureTesseract() {
    try {
      // Set default language (can be overridden per request)
      Language defaultLang = Language.fromString(defaultLanguage);
      tesseract.setLanguage(defaultLang.getTesseractCode());

      // Configure OCR engine mode and page segmentation
      tesseract.setOcrEngineMode(1); // Neural nets LSTM engine
      tesseract.setPageSegMode(1); // Automatic page segmentation with OSD

      // Additional configuration for better text recognition
      tesseract.setVariable("preserve_interword_spaces", "1");
      tesseract.setVariable("user_defined_dpi", "300");

      LOGGER.info("TesseractOCR configured with default language: {} ({})", 
                  defaultLang.getEnglishName(), defaultLang.getTesseractCode());

    } catch (Exception e) {
      LOGGER.warn(
          "Could not configure TesseractOCR, using default settings: {}",
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
    return extractTextFromPdf(pdfData, null);
  }

  /**
   * Extracts text from PDF using OCR with optional language specification
   *
   * @param pdfData PDF file data as byte array
   * @param language Optional language to use for OCR (null for auto-detection)
   * @return Extracted text
   * @throws TesseractException if OCR fails
   * @throws IOException if PDF processing fails
   */
  public String extractTextFromPdf(byte[] pdfData, Language language) throws TesseractException, IOException {
    LOGGER.debug("Starting OCR extraction for PDF data of size: {} bytes", pdfData.length);

    StringBuilder extractedText = new StringBuilder();
    Language detectedLanguage = null;

    try (PDDocument document = Loader.loadPDF(pdfData)) {
      PDFRenderer renderer = new PDFRenderer(document);
      int pageCount = document.getNumberOfPages();

      LOGGER.debug("Processing {} pages with OCR", pageCount);

      // First pass: extract some text to detect language if needed
      if (language == null && autoDetectLanguage && pageCount > 0) {
        try {
          BufferedImage firstPageImage = renderer.renderImageWithDPI(0, 300, ImageType.RGB);
          String firstPageText = tesseract.doOCR(firstPageImage);
          detectedLanguage = Language.detectFromContent(firstPageText);
          
          // Update tesseract language for better results
          tesseract.setLanguage(detectedLanguage.getTesseractCode());
          LOGGER.info("Auto-detected language: {} ({})", 
                     detectedLanguage.getEnglishName(), detectedLanguage.getTesseractCode());
        } catch (Exception e) {
          LOGGER.warn("Language auto-detection failed, using default: {}", e.getMessage());
        }
      } else if (language != null) {
        // Use specified language
        tesseract.setLanguage(language.getTesseractCode());
        detectedLanguage = language;
        LOGGER.info("Using specified language: {} ({})", 
                   language.getEnglishName(), language.getTesseractCode());
      }

      // Process all pages
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
    LOGGER.info("OCR extraction completed, extracted {} characters with language: {}", 
               result.length(), detectedLanguage != null ? detectedLanguage.getEnglishName() : "default");

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
    return extractTextFromImage(image, null);
  }

  /**
   * Extracts text from a single image with optional language specification
   *
   * @param image BufferedImage to process
   * @param language Optional language to use for OCR (null for default)
   * @return Extracted text
   * @throws TesseractException if OCR fails
   */
  public String extractTextFromImage(BufferedImage image, Language language) throws TesseractException {
    if (language != null) {
      tesseract.setLanguage(language.getTesseractCode());
      LOGGER.debug("Using language {} for image OCR", language.getEnglishName());
    }
    return tesseract.doOCR(image);
  }

  /**
   * Gets the currently configured language
   *
   * @return Current language configuration
   */
  public Language getCurrentLanguage() {
    return Language.fromString(defaultLanguage);
  }

  /**
   * Checks if auto-detection is enabled
   *
   * @return true if auto-detection is enabled
   */
  public boolean isAutoDetectEnabled() {
    return autoDetectLanguage;
  }

  /**
   * Extracts text from PDF using OCR and returns structured text with locations
   *
   * @param pdfData PDF file data as byte array
   * @return Structured text with location information
   * @throws TesseractException if OCR fails
   * @throws IOException if PDF processing fails
   */
  public StructuredText extractStructuredTextFromPdf(byte[] pdfData) throws TesseractException, IOException {
    return extractStructuredTextFromPdf(pdfData, null);
  }

  /**
   * Extracts text from PDF using OCR with optional language specification and returns structured text with locations
   *
   * @param pdfData PDF file data as byte array
   * @param language Optional language to use for OCR (null for auto-detection)
   * @return Structured text with location information
   * @throws TesseractException if OCR fails
   * @throws IOException if PDF processing fails
   */
  public StructuredText extractStructuredTextFromPdf(byte[] pdfData, Language language) throws TesseractException, IOException {
    LOGGER.debug("Starting OCR extraction with location data for PDF data of size: {} bytes", pdfData.length);

    StringBuilder fullTextBuilder = new StringBuilder();
    List<TextElement> allElements = new ArrayList<>();
    Language detectedLanguage = null;

    try (PDDocument document = Loader.loadPDF(pdfData)) {
      PDFRenderer renderer = new PDFRenderer(document);
      int pageCount = document.getNumberOfPages();

      LOGGER.debug("Processing {} pages with OCR for structured text", pageCount);

      // First pass: extract some text to detect language if needed
      if (language == null && autoDetectLanguage && pageCount > 0) {
        try {
          BufferedImage firstPageImage = renderer.renderImageWithDPI(0, 300, ImageType.RGB);
          String firstPageText = tesseract.doOCR(firstPageImage);
          detectedLanguage = Language.detectFromContent(firstPageText);
          
          // Update tesseract language for better results
          tesseract.setLanguage(detectedLanguage.getTesseractCode());
          LOGGER.info("Auto-detected language: {} ({})", 
                     detectedLanguage.getEnglishName(), detectedLanguage.getTesseractCode());
        } catch (Exception e) {
          LOGGER.warn("Language auto-detection failed, using default: {}", e.getMessage());
        }
      } else if (language != null) {
        // Use specified language
        tesseract.setLanguage(language.getTesseractCode());
        detectedLanguage = language;
        LOGGER.info("Using specified language: {} ({})", 
                   language.getEnglishName(), language.getTesseractCode());
      }

      // Process all pages and extract words with locations
      for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
        try {
          // Render PDF page as image at high DPI for better OCR
          BufferedImage image = renderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);

          // Extract words with location information (using constant 2 for word level)
          List<Word> words = tesseract.getWords(image, 2);
          
          if (words != null && !words.isEmpty()) {
            // Process words and build text elements with locations
            for (Word word : words) {
              String wordText = word.getText();
              if (wordText != null && !wordText.trim().isEmpty()) {
                // Create location for this word
                TextLocation location = TextLocation.of(
                    word.getBoundingBox().x,
                    word.getBoundingBox().y,
                    word.getBoundingBox().width,
                    word.getBoundingBox().height,
                    pageIndex + 1
                );
                
                // Create text element with location
                TextElement element = TextElement.withLocations(wordText, List.of(location));
                allElements.add(element);
                
                // Add to full text
                fullTextBuilder.append(wordText).append(" ");
              }
            }
            
            // Add page separator if not last page
            if (pageIndex < pageCount - 1) {
              fullTextBuilder.append("\n\n--- Page ").append(pageIndex + 2).append(" ---\n\n");
            }
          }

          LOGGER.debug("OCR completed for page {}/{} with {} words", pageIndex + 1, pageCount, words != null ? words.size() : 0);

        } catch (Exception e) {
          LOGGER.warn("OCR failed for page {}: {}", pageIndex + 1, e.getMessage());
          // Continue with other pages
        }
      }
    }

    String fullText = fullTextBuilder.toString().trim();
    LOGGER.info("OCR extraction completed, extracted {} characters with {} text elements and language: {}", 
               fullText.length(), allElements.size(), detectedLanguage != null ? detectedLanguage.getEnglishName() : "default");

    return StructuredText.fromElements(fullText, allElements);
  }
}
