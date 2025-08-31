package fi.sutinse.pdfextractor.model;

/** Enum for supported languages in the PDF text extractor */
public enum Language {
  FINNISH("fin", "Finnish", "Suomi"),
  SWEDISH("swe", "Swedish", "Svenska"), 
  ENGLISH("eng", "English", "English");

  private final String tesseractCode;
  private final String englishName;
  private final String nativeName;

  Language(String tesseractCode, String englishName, String nativeName) {
    this.tesseractCode = tesseractCode;
    this.englishName = englishName;
    this.nativeName = nativeName;
  }

  public String getTesseractCode() {
    return tesseractCode;
  }

  public String getEnglishName() {
    return englishName;
  }

  public String getNativeName() {
    return nativeName;
  }

  /**
   * Detects language based on content patterns
   *
   * @param text The text content to analyze
   * @return Detected language, defaults to FINNISH if uncertain
   */
  public static Language detectFromContent(String text) {
    if (text == null || text.trim().isEmpty()) {
      return FINNISH; // Default fallback
    }

    String lowerText = text.toLowerCase();
    
    // Count language-specific indicators
    int finnishScore = countFinnishIndicators(lowerText);
    int swedishScore = countSwedishIndicators(lowerText);
    int englishScore = countEnglishIndicators(lowerText);

    // Return language with highest score
    if (swedishScore > finnishScore && swedishScore > englishScore) {
      return SWEDISH;
    } else if (englishScore > finnishScore && englishScore > swedishScore) {
      return ENGLISH;
    } else {
      return FINNISH; // Default to Finnish
    }
  }

  private static int countFinnishIndicators(String text) {
    int score = 0;
    // Finnish-specific words and patterns
    String[] finnishWords = {
      "ja", "on", "tai", "että", "kuten", "kanssa", "ilman", "mukaan", "sitten",
      "lasku", "maksu", "eräpäivä", "yhteensä", "alv", "arvonlisävero", "kuitti",
      "sopimus", "todistus", "raportti", "lomake", "kirje", "käyttöohje",
      "erittely", "hinta", "summa", "päivämäärä", "nimi", "osoite"
    };
    
    for (String word : finnishWords) {
      score += countWordOccurrences(text, word);
    }
    
    // Finnish character patterns
    score += text.length() - text.replace("ä", "").length();
    score += text.length() - text.replace("ö", "").length();
    score += (text.length() - text.replace("å", "").length()) / 2; // Less common in Finnish
    
    return score;
  }

  private static int countSwedishIndicators(String text) {
    int score = 0;
    // Swedish-specific words and patterns  
    String[] swedishWords = {
      "och", "är", "att", "för", "med", "utan", "enligt", "sedan", "när",
      "faktura", "betalning", "förfallodag", "totalt", "moms", "kvitto",
      "kontrakt", "certifikat", "rapport", "blankett", "brev", "manual",
      "specifikation", "pris", "summa", "datum", "namn", "adress"
    };
    
    for (String word : swedishWords) {
      score += countWordOccurrences(text, word);
    }
    
    // Swedish character patterns
    score += text.length() - text.replace("å", "").length();
    score += text.length() - text.replace("ä", "").length();
    score += text.length() - text.replace("ö", "").length();
    
    return score;
  }

  private static int countEnglishIndicators(String text) {
    int score = 0;
    // English-specific words and patterns
    String[] englishWords = {
      "and", "is", "the", "that", "with", "without", "according", "then", "when",
      "invoice", "payment", "due", "total", "tax", "vat", "receipt",
      "contract", "certificate", "report", "form", "letter", "manual",
      "specification", "price", "amount", "date", "name", "address"
    };
    
    for (String word : englishWords) {
      score += countWordOccurrences(text, word);
    }
    
    // Bonus for common English patterns
    if (text.contains("the ")) score += 2;
    if (text.contains(" and ")) score += 2;
    if (text.contains(" of ")) score += 2;
    
    return score;
  }

  private static int countWordOccurrences(String text, String word) {
    String pattern = "\\b" + word + "\\b";
    return (text.length() - text.replaceAll(pattern, "").length()) / word.length();
  }

  /**
   * Parse language from string (case-insensitive)
   *
   * @param languageStr Language string (tesseract code, English name, or native name)
   * @return Corresponding Language enum or FINNISH as default
   */
  public static Language fromString(String languageStr) {
    if (languageStr == null || languageStr.trim().isEmpty()) {
      return FINNISH;
    }
    
    String normalized = languageStr.trim().toLowerCase();
    
    for (Language lang : values()) {
      if (lang.tesseractCode.equals(normalized) ||
          lang.englishName.toLowerCase().equals(normalized) ||
          lang.nativeName.toLowerCase().equals(normalized)) {
        return lang;
      }
    }
    
    return FINNISH; // Default fallback
  }
}