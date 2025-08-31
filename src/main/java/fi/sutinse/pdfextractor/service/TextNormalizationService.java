package fi.sutinse.pdfextractor.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Service for normalizing Finnish text, especially for OCR results
 */
@ApplicationScoped
public class TextNormalizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextNormalizationService.class);

    // Common OCR errors in Finnish text
    private static final Pattern[] FINNISH_CORRECTIONS = {
        // Common character substitutions
        Pattern.compile("([0-9])O([0-9])"), // 0 -> O confusion in numbers
        Pattern.compile("([a-zA-Z])0([a-zA-Z])"), // O -> 0 confusion in words
        Pattern.compile("l1"), // l -> 1 confusion
        Pattern.compile("I1"), // I -> 1 confusion
        Pattern.compile("rn"), // r+n -> m confusion
        Pattern.compile("vv"), // w -> vv confusion
        
        // Finnish specific corrections
        Pattern.compile("ä"), // Ensure ä is properly encoded
        Pattern.compile("ö"), // Ensure ö is properly encoded
        Pattern.compile("å"), // Ensure å is properly encoded
    };

    private static final String[] FINNISH_REPLACEMENTS = {
        "$100$2", // Fix 0/O in numbers
        "$1O$2", // Fix O/0 in words
        "ll", // Fix l/1
        "Il", // Fix I/1
        "m", // Fix rn/m
        "w", // Fix vv/w
        "ä", // Normalize ä
        "ö", // Normalize ö
        "å", // Normalize å
    };

    /**
     * Normalizes text for Finnish language, especially useful for OCR results
     *
     * @param text Input text to normalize
     * @return Normalized text
     */
    public String normalizeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        LOGGER.debug("Normalizing text of length: {}", text.length());

        // Step 1: Unicode normalization
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFC);

        // Step 2: Whitespace normalization
        normalized = normalizeWhitespace(normalized);

        // Step 3: Finnish-specific OCR corrections
        normalized = applyFinnishCorrections(normalized);

        // Step 4: Invoice-specific normalization (if detected as invoice)
        normalized = normalizeInvoiceText(normalized);

        LOGGER.debug("Text normalization completed, length: {} -> {}", 
                    text.length(), normalized.length());

        return normalized;
    }

    /**
     * Normalizes whitespace characters
     */
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

    /**
     * Applies Finnish-specific corrections for common OCR errors
     */
    private String applyFinnishCorrections(String text) {
        String corrected = text;
        
        for (int i = 0; i < FINNISH_CORRECTIONS.length && i < FINNISH_REPLACEMENTS.length; i++) {
            corrected = FINNISH_CORRECTIONS[i].matcher(corrected)
                       .replaceAll(FINNISH_REPLACEMENTS[i]);
        }
        
        // Fix common Finnish word OCR errors
        corrected = corrected.replaceAll("\\bja\\b", "ja"); // Ensure 'ja' (and) is correct
        corrected = corrected.replaceAll("\\bvai\\b", "vai"); // Ensure 'vai' (or) is correct
        corrected = corrected.replaceAll("\\bon\\b", "on"); // Ensure 'on' (is) is correct
        
        return corrected;
    }

    /**
     * Applies invoice-specific normalization
     */
    private String normalizeInvoiceText(String text) {
        String invoiceText = text;
        
        // Normalize common invoice terms
        invoiceText = invoiceText.replaceAll("(?i)lasku\\s*(?:numero|nro|#)", "Laskunumero:");
        invoiceText = invoiceText.replaceAll("(?i)eräpäivä", "Eräpäivä:");
        invoiceText = invoiceText.replaceAll("(?i)yhteensä", "Yhteensä:");
        invoiceText = invoiceText.replaceAll("(?i)alv\\s*%?", "ALV");
        invoiceText = invoiceText.replaceAll("(?i)arvonlisävero", "Arvonlisävero");
        
        // Normalize monetary amounts
        invoiceText = invoiceText.replaceAll("(\\d+)[,.]([0-9]{2})\\s*€", "$1,$2 €");
        invoiceText = invoiceText.replaceAll("€\\s*(\\d+[,.][0-9]{2})", "$1 €");
        
        // Normalize dates
        invoiceText = invoiceText.replaceAll("(\\d{1,2})[./](\\d{1,2})[./](\\d{4})", "$1.$2.$3");
        
        return invoiceText;
    }

    /**
     * Cleans text by removing obvious OCR artifacts
     */
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