package fi.sutinse.pdfextractor.service;

import fi.sutinse.pdfextractor.model.DocumentSchema;
import fi.sutinse.pdfextractor.model.DocumentType;
import fi.sutinse.pdfextractor.model.Language;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for extracting structured data from text based on document schemas */
@ApplicationScoped
public class StructuredDataExtractionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StructuredDataExtractionService.class);

  /**
   * Extracts structured data from text using document type schema
   *
   * @param text Raw extracted text
   * @param documentType Detected document type
   * @param language Detected language
   * @return Map of structured field data
   */
  public Map<String, Object> extractStructuredData(
      String text, DocumentType documentType, Language language) {
    if (text == null || text.trim().isEmpty()) {
      LOGGER.debug("No text provided for structured extraction");
      return Map.of();
    }

    LOGGER.debug(
        "Extracting structured data for document type: {}, language: {}", documentType, language);

    Map<String, DocumentSchema.FieldSchema> schema =
        DocumentSchema.getSchemaForDocumentType(documentType);
    Map<String, Object> structuredData = new HashMap<>();

    for (Map.Entry<String, DocumentSchema.FieldSchema> entry : schema.entrySet()) {
      String fieldKey = entry.getKey();
      DocumentSchema.FieldSchema fieldSchema = entry.getValue();

      String extractedValue = extractFieldValue(text, fieldSchema, language);
      if (extractedValue != null && !extractedValue.trim().isEmpty()) {
        structuredData.put(fieldKey, extractedValue.trim());
        LOGGER.debug("Extracted field '{}': '{}'", fieldKey, extractedValue.trim());
      }
    }

    LOGGER.debug("Extracted {} structured fields from text", structuredData.size());
    return structuredData;
  }

  /**
   * Extracts value for a specific field from text
   *
   * @param text Raw text content
   * @param fieldSchema Schema for the field
   * @param language Detected language
   * @return Extracted value or null if not found
   */
  private String extractFieldValue(
      String text, DocumentSchema.FieldSchema fieldSchema, Language language) {

    List<String> patterns = getPatternsByLanguage(fieldSchema, language);

    // Try primary language patterns first
    for (String pattern : patterns) {
      String value = extractValueWithPattern(text, pattern);
      if (value != null) {
        return value;
      }
    }

    // If no match found with primary language, try all patterns as fallback
    if (patterns.size() < (fieldSchema.finnishPatterns().size() + 
                           fieldSchema.swedishPatterns().size() + 
                           fieldSchema.englishPatterns().size())) {
      var allPatterns = new java.util.ArrayList<String>();
      allPatterns.addAll(fieldSchema.finnishPatterns());
      allPatterns.addAll(fieldSchema.swedishPatterns());
      allPatterns.addAll(fieldSchema.englishPatterns());
      
      for (String pattern : allPatterns) {
        String value = extractValueWithPattern(text, pattern);
        if (value != null) {
          return value;
        }
      }
    }

    return null;
  }

  /**
   * Gets appropriate patterns based on detected language
   *
   * @param fieldSchema Field schema containing patterns for all languages
   * @param language Detected language
   * @return List of patterns to try
   */
  private List<String> getPatternsByLanguage(
      DocumentSchema.FieldSchema fieldSchema, Language language) {
    return switch (language) {
      case FINNISH -> fieldSchema.finnishPatterns();
      case SWEDISH -> fieldSchema.swedishPatterns();
      case ENGLISH -> fieldSchema.englishPatterns();
      default -> {
        // Try all patterns if detection failed or unknown language
        var allPatterns = new java.util.ArrayList<String>();
        allPatterns.addAll(fieldSchema.finnishPatterns());
        allPatterns.addAll(fieldSchema.swedishPatterns());
        allPatterns.addAll(fieldSchema.englishPatterns());
        yield allPatterns;
      }
    };
  }

  /**
   * Extracts value using pattern matching
   *
   * @param text Raw text content
   * @param fieldPattern Pattern to match (e.g., "lasku numero")
   * @return Extracted value or null if not found
   */
  private String extractValueWithPattern(String text, String fieldPattern) {
    // Create regex patterns for different value extraction scenarios
    
    // Pattern 1: "field: value" or "field value" 
    Pattern colonPattern = Pattern.compile(
        "(?i)" + Pattern.quote(fieldPattern) + "\\s*:?\\s*([^\\n\\r]*)", 
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    
    Matcher matcher = colonPattern.matcher(text);
    if (matcher.find()) {
      String value = matcher.group(1).trim();
      if (!value.isEmpty()) {
        return cleanExtractedValue(value);
      }
    }

    // Pattern 2: Look for numeric values after the field name
    Pattern numericPattern = Pattern.compile(
        "(?i)" + Pattern.quote(fieldPattern) + "\\s*:?\\s*([0-9]+[.,]?[0-9]*)", 
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    
    matcher = numericPattern.matcher(text);
    if (matcher.find()) {
      return cleanExtractedValue(matcher.group(1).trim());
    }

    // Pattern 3: Look for date patterns after field name
    Pattern datePattern = Pattern.compile(
        "(?i)" + Pattern.quote(fieldPattern) + "\\s*:?\\s*([0-9]{1,2}[./-][0-9]{1,2}[./-][0-9]{2,4})", 
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    
    matcher = datePattern.matcher(text);
    if (matcher.find()) {
      return cleanExtractedValue(matcher.group(1).trim());
    }

    // Pattern 4: Look for currency amounts after field name
    Pattern currencyPattern = Pattern.compile(
        "(?i)" + Pattern.quote(fieldPattern) + "\\s*:?\\s*([0-9]+[.,][0-9]{2}\\s*[€$£]?)", 
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    
    matcher = currencyPattern.matcher(text);
    if (matcher.find()) {
      return cleanExtractedValue(matcher.group(1).trim());
    }

    return null;
  }

  /**
   * Cleans and normalizes extracted values
   *
   * @param value Raw extracted value
   * @return Cleaned value
   */
  private String cleanExtractedValue(String value) {
    if (value == null) {
      return null;
    }

    // Remove common trailing characters that shouldn't be part of the value
    value = value.replaceAll("[,;.\\s]+$", "");
    
    // Remove leading/trailing whitespace
    value = value.trim();
    
    // Replace multiple spaces with single space
    value = value.replaceAll("\\s+", " ");
    
    // Don't return empty values
    if (value.isEmpty()) {
      return null;
    }

    return value;
  }
}