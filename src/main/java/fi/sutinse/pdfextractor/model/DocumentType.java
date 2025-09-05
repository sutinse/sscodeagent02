package fi.sutinse.pdfextractor.model;

/** Enum for commonly recognized document types */
public enum DocumentType {
  INVOICE("Lasku", "Faktura", "Invoice"),
  RECEIPT("Kuitti", "Kvitto", "Receipt"),
  CONTRACT("Sopimus", "Kontrakt", "Contract"),
  CERTIFICATE("Todistus", "Certifikat", "Certificate"),
  REPORT("Raportti", "Rapport", "Report"),
  FORM("Lomake", "Blankett", "Form"),
  LETTER("Kirje", "Brev", "Letter"),
  MANUAL("Käyttöohje", "Manual", "Manual"),
  SPECIFICATION("Erittely", "Specifikation", "Specification"),
  UNKNOWN("Tuntematon", "Okänd", "Unknown");

  private final String finnishName;
  private final String swedishName;
  private final String englishName;

  DocumentType(String finnishName, String swedishName, String englishName) {
    this.finnishName = finnishName;
    this.swedishName = swedishName;
    this.englishName = englishName;
  }

  public String getFinnishName() {
    return finnishName;
  }

  public String getSwedishName() {
    return swedishName;
  }

  public String getEnglishName() {
    return englishName;
  }

  public String getLocalizedName(Language language) {
    switch (language) {
      case FINNISH:
        return finnishName;
      case SWEDISH:
        return swedishName;
      case ENGLISH:
        return englishName;
      default:
        return englishName;
    }
  }

  /**
   * Detects document type based on content with multi-language support
   *
   * @param text The extracted text content
   * @return Detected document type
   */
  public static DocumentType detectFromContent(String text) {
    return detectFromContent(text, Language.detectFromContent(text));
  }

  /**
   * Detects document type based on content for a specific language
   *
   * @param text The extracted text content
   * @param language The language to use for detection
   * @return Detected document type
   */
  public static DocumentType detectFromContent(String text, Language language) {
    if (text == null || text.trim().isEmpty()) {
      return UNKNOWN;
    }

    String lowerText = text.toLowerCase();

    // Invoice detection patterns (multi-language)
    if (containsAny(lowerText, getInvoiceKeywords(language))) {
      return INVOICE;
    }

    // Receipt detection patterns (multi-language)
    if (containsAny(lowerText, getReceiptKeywords(language))) {
      return RECEIPT;
    }

    // Contract detection patterns (multi-language)
    if (containsAny(lowerText, getContractKeywords(language))) {
      return CONTRACT;
    }

    // Certificate detection patterns (multi-language)
    if (containsAny(lowerText, getCertificateKeywords(language))) {
      return CERTIFICATE;
    }

    // Report detection patterns (multi-language)
    if (containsAny(lowerText, getReportKeywords(language))) {
      return REPORT;
    }

    // Form detection patterns (multi-language)
    if (containsAny(lowerText, getFormKeywords(language))) {
      return FORM;
    }

    // Letter detection patterns (multi-language)
    if (containsAny(lowerText, getLetterKeywords(language))) {
      return LETTER;
    }

    // Manual detection patterns (multi-language)
    if (containsAny(lowerText, getManualKeywords(language))) {
      return MANUAL;
    }

    // Specification detection patterns (multi-language)
    if (containsAny(lowerText, getSpecificationKeywords(language))) {
      return SPECIFICATION;
    }

    return UNKNOWN;
  }

  private static String[] getInvoiceKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {
            "lasku",
            "laskun",
            "laskutus",
            "maksettava",
            "eräpäivä",
            "yhteensä",
            "alv",
            "arvonlisävero",
            "summa",
            "hinta",
            "maksu"
          };
      case SWEDISH ->
          new String[] {
            "faktura",
            "betalning",
            "förfallodag",
            "totalt",
            "moms",
            "summa",
            "pris",
            "betalas",
            "fakturabelopp",
            "avgift"
          };
      case ENGLISH ->
          new String[] {
            "invoice", "payment", "due", "total", "amount", "vat", "tax", "bill", "charge", "cost",
            "price", "fee"
          };
    };
  }

  private static String[] getReceiptKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {
            "kuitti", "ostos", "kassalaite", "kassa", "myymälä", "kauppa", "osto", "maksutapa"
          };
      case SWEDISH ->
          new String[] {
            "kvitto", "inköp", "kassa", "butik", "köp", "affär", "betalningsmetod", "handel"
          };
      case ENGLISH ->
          new String[] {
            "receipt",
            "purchase",
            "store",
            "shop",
            "transaction",
            "payment method",
            "bought",
            "retail"
          };
    };
  }

  private static String[] getContractKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {
            "sopimus", "sopimusehto", "osapuoli", "allekirjoitus", "sitoumus", "velvoite"
          };
      case SWEDISH ->
          new String[] {
            "kontrakt", "avtal", "part", "underskrift", "åtagande", "skyldighet", "villkor"
          };
      case ENGLISH ->
          new String[] {
            "contract", "agreement", "party", "signature", "obligation", "terms", "conditions"
          };
    };
  }

  private static String[] getCertificateKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {"todistus", "sertifikaatti", "diplomi", "pätevyys", "koulutus", "suoritus"};
      case SWEDISH ->
          new String[] {"certifikat", "intyg", "diplom", "kompetens", "utbildning", "genomförande"};
      case ENGLISH ->
          new String[] {
            "certificate", "diploma", "qualification", "training", "completion", "achievement"
          };
    };
  }

  private static String[] getReportKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {"raportti", "selvitys", "analyysi", "tutkimus", "yhteenveto", "tilasto"};
      case SWEDISH ->
          new String[] {
            "rapport", "utredning", "analys", "forskning", "sammanfattning", "statistik"
          };
      case ENGLISH ->
          new String[] {"report", "analysis", "research", "summary", "statistics", "findings"};
    };
  }

  private static String[] getFormKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {"lomake", "hakemus", "ilmoitus", "rekisteröinti", "täytä", "allekirjoita"};
      case SWEDISH ->
          new String[] {"blankett", "ansökan", "anmälan", "registrering", "fyll", "underteckna"};
      case ENGLISH ->
          new String[] {"form", "application", "registration", "fill", "sign", "submit"};
    };
  }

  private static String[] getLetterKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {
            "kirje", "viesti", "tervehdys", "kunnioittaen", "ystävällisin", "yhteistyöterveisin"
          };
      case SWEDISH ->
          new String[] {
            "brev",
            "meddelande",
            "hälsning",
            "med vänliga hälsningar",
            "högaktningsfullt",
            "vänligen"
          };
      case ENGLISH ->
          new String[] {"letter", "message", "greeting", "sincerely", "regards", "yours"};
    };
  }

  private static String[] getManualKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {"käyttöohje", "ohje", "opas", "manual", "instructions", "vaihe", "askel"};
      case SWEDISH ->
          new String[] {"manual", "instruktion", "guide", "anvisning", "steg", "procedur"};
      case ENGLISH ->
          new String[] {"manual", "instructions", "guide", "procedure", "step", "how to"};
    };
  }

  private static String[] getSpecificationKeywords(Language language) {
    return switch (language) {
      case FINNISH ->
          new String[] {
            "erittely", "listaus", "luettelo", "yksityiskohta", "spesifikaatio", "määrittely"
          };
      case SWEDISH ->
          new String[] {
            "specifikation", "lista", "förteckning", "detalj", "specificering", "uppräkning"
          };
      case ENGLISH ->
          new String[] {
            "specification", "listing", "details", "itemization", "breakdown", "enumeration"
          };
    };
  }

  private static boolean containsAny(String text, String... keywords) {
    return java.util.Arrays.stream(keywords).anyMatch(text::contains);
  }
}
