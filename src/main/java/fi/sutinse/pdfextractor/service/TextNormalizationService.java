package fi.sutinse.pdfextractor.service;

import fi.sutinse.pdfextractor.model.Language;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.Normalizer;
import java.util.List;
import java.util.SequencedCollection;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Service for normalizing text for multiple languages, especially for OCR results */
@ApplicationScoped
public class TextNormalizationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TextNormalizationService.class);

  // Common OCR errors in Nordic/European text using JDK 21 Sequenced Collections
  private static final SequencedCollection<Pattern> COMMON_CORRECTIONS =
      List.of(
          // Common character substitutions
          Pattern.compile("([0-9])O([0-9])"), // 0 -> O confusion in numbers
          Pattern.compile("([a-zA-Z])0([a-zA-Z])"), // O -> 0 confusion in words
          Pattern.compile("l1"), // l -> 1 confusion
          Pattern.compile("I1"), // I -> 1 confusion
          Pattern.compile("rn"), // r+n -> m confusion
          Pattern.compile("vv") // w -> vv confusion
          );

  private static final SequencedCollection<String> COMMON_REPLACEMENTS =
      List.of(
          "$100$2", // Fix 0/O in numbers
          "$1O$2", // Fix O/0 in words
          "ll", // Fix l/1
          "Il", // Fix I/1
          "m", // Fix rn/m
          "w" // Fix vv/w
          );

  // Finnish specific corrections using JDK 21 Sequenced Collections
  private static final SequencedCollection<Pattern> FINNISH_CORRECTIONS =
      List.of(
          Pattern.compile("ä"), // Ensure ä is properly encoded
          Pattern.compile("ö"), // Ensure ö is properly encoded
          Pattern.compile("å") // Ensure å is properly encoded
          );

  private static final SequencedCollection<String> FINNISH_REPLACEMENTS =
      List.of(
          "ä", // Normalize ä
          "ö", // Normalize ö
          "å" // Normalize å
          );

  // Swedish specific corrections using JDK 21 Sequenced Collections
  private static final SequencedCollection<Pattern> SWEDISH_CORRECTIONS =
      List.of(
          Pattern.compile("ä"), // Ensure ä is properly encoded
          Pattern.compile("ö"), // Ensure ö is properly encoded
          Pattern.compile("å") // Ensure å is properly encoded
          );

  private static final SequencedCollection<String> SWEDISH_REPLACEMENTS =
      List.of(
          "ä", // Normalize ä
          "ö", // Normalize ö
          "å" // Normalize å
          );

  /**
   * Normalizes text for a specific language, especially useful for OCR results
   *
   * @param text Input text to normalize
   * @param language Language to use for normalization
   * @return Normalized text
   */
  public String normalizeText(String text, Language language) {
    if (text == null || text.trim().isEmpty()) {
      return text;
    }

    LOGGER.debug(
        "Normalizing text of length: {} for language: {}",
        text.length(),
        language.getEnglishName());

    // Step 1: Unicode normalization
    String normalized = Normalizer.normalize(text, Normalizer.Form.NFC);

    // Step 2: Whitespace normalization
    normalized = normalizeWhitespace(normalized);

    // Step 3: Common OCR corrections
    normalized = applyCommonCorrections(normalized);

    // Step 4: Language-specific OCR corrections
    normalized = applyLanguageSpecificCorrections(normalized, language);

    // Step 5: Document type specific normalization (if detected as invoice)
    normalized = normalizeDocumentText(normalized, language);

    LOGGER.debug(
        "Text normalization completed, length: {} -> {}", text.length(), normalized.length());

    return normalized;
  }

  /**
   * Normalizes text with automatic language detection
   *
   * @param text Input text to normalize
   * @return Normalized text
   */
  public String normalizeText(String text) {
    Language detectedLanguage = Language.detectFromContent(text);
    return normalizeText(text, detectedLanguage);
  }

  /** Normalizes whitespace characters */
  private String normalizeWhitespace(String text) {
    // Replace multiple consecutive whitespace with single space
    text = text.replaceAll("\\s+", " ");

    // Remove whitespace at start and end of lines
    text = text.replaceAll("(?m)^\\s+|\\s+$", "");

    // Normalize line breaks
    text = text.replaceAll("\\r\\n|\\r", "\n");

    // Remove excessive empty lines
    text = text.replaceAll("\\n{3,}", "\n\n");

    return text.trim();
  }

  /** Applies common corrections for OCR errors using JDK 21 Sequenced Collections */
  private String applyCommonCorrections(String text) {
    String corrected = text;

    // Use JDK 21 Sequenced Collections features - get first/last elements efficiently
    var corrections = COMMON_CORRECTIONS.stream().toList();
    var replacements = COMMON_REPLACEMENTS.stream().toList();

    for (int i = 0; i < corrections.size() && i < replacements.size(); i++) {
      corrected = corrections.get(i).matcher(corrected).replaceAll(replacements.get(i));
    }

    return corrected;
  }

  /** Applies language-specific corrections for common OCR errors */
  private String applyLanguageSpecificCorrections(String text, Language language) {
    return switch (language) {
      case FINNISH -> applyFinnishCorrections(text);
      case SWEDISH -> applySwedishCorrections(text);
      case ENGLISH -> applyEnglishCorrections(text);
    };
  }

  /** Applies Finnish-specific corrections for common OCR errors using JDK 21 features */
  private String applyFinnishCorrections(String text) {
    String corrected = text;

    // Use JDK 21 Sequenced Collections for efficient iteration
    var corrections = FINNISH_CORRECTIONS.stream().toList();
    var replacements = FINNISH_REPLACEMENTS.stream().toList();

    for (int i = 0; i < corrections.size() && i < replacements.size(); i++) {
      corrected = corrections.get(i).matcher(corrected).replaceAll(replacements.get(i));
    }

    // Fix common Finnish word OCR errors
    corrected = corrected.replaceAll("\\bja\\b", "ja"); // Ensure 'ja' (and) is correct
    corrected = corrected.replaceAll("\\bvai\\b", "vai"); // Ensure 'vai' (or) is correct
    corrected = corrected.replaceAll("\\bon\\b", "on"); // Ensure 'on' (is) is correct

    return corrected;
  }

  /** Applies Swedish-specific corrections for common OCR errors using JDK 21 features */
  private String applySwedishCorrections(String text) {
    String corrected = text;

    // Use JDK 21 Sequenced Collections for efficient iteration
    var corrections = SWEDISH_CORRECTIONS.stream().toList();
    var replacements = SWEDISH_REPLACEMENTS.stream().toList();

    for (int i = 0; i < corrections.size() && i < replacements.size(); i++) {
      corrected = corrections.get(i).matcher(corrected).replaceAll(replacements.get(i));
    }

    // Fix common Swedish word OCR errors
    corrected = corrected.replaceAll("\\boch\\b", "och"); // Ensure 'och' (and) is correct
    corrected = corrected.replaceAll("\\beller\\b", "eller"); // Ensure 'eller' (or) is correct
    corrected = corrected.replaceAll("\\bär\\b", "är"); // Ensure 'är' (is) is correct

    return corrected;
  }

  /** Applies English-specific corrections for common OCR errors */
  private String applyEnglishCorrections(String text) {
    String corrected = text;

    // Fix common English word OCR errors
    corrected = corrected.replaceAll("\\bthe\\b", "the"); // Ensure 'the' is correct
    corrected = corrected.replaceAll("\\band\\b", "and"); // Ensure 'and' is correct
    corrected = corrected.replaceAll("\\bor\\b", "or"); // Ensure 'or' is correct
    corrected = corrected.replaceAll("\\bis\\b", "is"); // Ensure 'is' is correct

    return corrected;
  }

  /** Applies document type specific normalization based on language */
  private String normalizeDocumentText(String text, Language language) {
    switch (language) {
      case FINNISH:
        return normalizeFinnishDocumentText(text);
      case SWEDISH:
        return normalizeSwedishDocumentText(text);
      case ENGLISH:
        return normalizeEnglishDocumentText(text);
      default:
        return text;
    }
  }

  /** Applies Finnish document-specific normalization */
  private String normalizeFinnishDocumentText(String text) {
    String normalized = text;

    // Normalize common invoice terms
    normalized = normalized.replaceAll("(?i)lasku\\s*(?:numero|nro|#)", "Laskunumero:");
    normalized = normalized.replaceAll("(?i)eräpäivä", "Eräpäivä:");
    normalized = normalized.replaceAll("(?i)yhteensä", "Yhteensä:");
    normalized = normalized.replaceAll("(?i)alv\\s*%?", "ALV");
    normalized = normalized.replaceAll("(?i)arvonlisävero", "Arvonlisävero");

    // Normalize monetary amounts
    normalized = normalized.replaceAll("(\\d+)[,.]([0-9]{2})\\s*€", "$1,$2 €");
    normalized = normalized.replaceAll("€\\s*(\\d+[,.][0-9]{2})", "$1 €");

    // Normalize dates
    normalized = normalized.replaceAll("(\\d{1,2})[./](\\d{1,2})[./](\\d{4})", "$1.$2.$3");

    return normalized;
  }

  /** Applies Swedish document-specific normalization */
  private String normalizeSwedishDocumentText(String text) {
    String normalized = text;

    // Normalize common invoice terms in Swedish
    normalized = normalized.replaceAll("(?i)faktura\\s*(?:nummer|nr|#)", "Fakturanummer:");
    normalized = normalized.replaceAll("(?i)förfallodag", "Förfallodag:");
    normalized = normalized.replaceAll("(?i)totalt", "Totalt:");
    normalized = normalized.replaceAll("(?i)moms\\s*%?", "Moms");

    // Normalize monetary amounts (Swedish uses SEK)
    normalized = normalized.replaceAll("(\\d+)[,.]([0-9]{2})\\s*(?:kr|SEK)", "$1,$2 kr");
    normalized = normalized.replaceAll("(?:kr|SEK)\\s*(\\d+[,.][0-9]{2})", "$1 kr");

    // Normalize dates
    normalized = normalized.replaceAll("(\\d{1,2})[./](\\d{1,2})[./](\\d{4})", "$1.$2.$3");

    return normalized;
  }

  /** Applies English document-specific normalization */
  private String normalizeEnglishDocumentText(String text) {
    String normalized = text;

    // Normalize common invoice terms in English
    normalized = normalized.replaceAll("(?i)invoice\\s*(?:number|no|#)", "Invoice Number:");
    normalized = normalized.replaceAll("(?i)due\\s*date", "Due Date:");
    normalized = normalized.replaceAll("(?i)total", "Total:");
    normalized = normalized.replaceAll("(?i)vat\\s*%?", "VAT");
    normalized = normalized.replaceAll("(?i)tax", "Tax");

    // Normalize monetary amounts
    normalized = normalized.replaceAll("\\$\\s*(\\d+[,.][0-9]{2})", "$1 USD");
    normalized = normalized.replaceAll("(\\d+)[,.]([0-9]{2})\\s*\\$", "$1.$2 USD");

    // Normalize dates (MM/DD/YYYY to DD.MM.YYYY)
    normalized = normalized.replaceAll("(\\d{1,2})/(\\d{1,2})/(\\d{4})", "$2.$1.$3");

    return normalized;
  }

  /** Cleans text by removing obvious OCR artifacts */
  public String cleanOcrArtifacts(String text) {
    if (text == null) {
      return null;
    }

    // Remove obvious OCR noise
    String cleaned = text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");

    // Remove isolated single characters that are likely OCR errors
    cleaned = cleaned.replaceAll("\\b[^a-zA-ZäöåÄÖÅ0-9]\\b", " ");

    // Remove excessive punctuation
    cleaned = cleaned.replaceAll("[.]{3,}", "...");
    cleaned = cleaned.replaceAll("[-]{3,}", "---");

    return normalizeWhitespace(cleaned);
  }
}
