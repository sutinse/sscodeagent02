package fi.sutinse.pdfextractor.model;

/** Enum for commonly recognized document types */
public enum DocumentType {
  INVOICE("Lasku"),
  RECEIPT("Kuitti"),
  CONTRACT("Sopimus"),
  CERTIFICATE("Todistus"),
  REPORT("Raportti"),
  FORM("Lomake"),
  LETTER("Kirje"),
  MANUAL("Käyttöohje"),
  SPECIFICATION("Erittely"),
  UNKNOWN("Tuntematon");

  private final String finnishName;

  DocumentType(String finnishName) {
    this.finnishName = finnishName;
  }

  public String getFinnishName() {
    return finnishName;
  }

  /**
   * Detects document type based on content
   *
   * @param text The extracted text content
   * @return Detected document type
   */
  public static DocumentType detectFromContent(String text) {
    if (text == null || text.trim().isEmpty()) {
      return UNKNOWN;
    }

    String lowerText = text.toLowerCase();

    // Invoice detection patterns (Finnish)
    if (containsAny(
        lowerText,
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
        "maksu")) {
      return INVOICE;
    }

    // Receipt detection patterns
    if (containsAny(
        lowerText,
        "kuitti",
        "ostos",
        "kassalaite",
        "kassa",
        "myymälä",
        "kauppa",
        "osto",
        "maksutapa")) {
      return RECEIPT;
    }

    // Contract detection patterns
    if (containsAny(
        lowerText, "sopimus", "sopimusehto", "osapuoli", "allekirjoitus", "sitoumus", "velvoite")) {
      return CONTRACT;
    }

    // Certificate detection patterns
    if (containsAny(
        lowerText, "todistus", "sertifikaatti", "diplomí", "pätevyys", "koulutus", "suoritus")) {
      return CERTIFICATE;
    }

    // Report detection patterns
    if (containsAny(
        lowerText, "raportti", "selvitys", "analyysi", "tutkimus", "yhteenveto", "tilasto")) {
      return REPORT;
    }

    // Form detection patterns
    if (containsAny(
        lowerText, "lomake", "hakemus", "ilmoitus", "rekisteröinti", "täytä", "allekirjoita")) {
      return FORM;
    }

    // Letter detection patterns
    if (containsAny(
        lowerText,
        "kirje",
        "viesti",
        "tervehdys",
        "kunnioittaen",
        "ystävällisin",
        "yhteistyöterveisin")) {
      return LETTER;
    }

    // Manual detection patterns
    if (containsAny(
        lowerText, "käyttöohje", "ohje", "opas", "manual", "instructions", "vaihe", "askel")) {
      return MANUAL;
    }

    // Specification detection patterns
    if (containsAny(
        lowerText,
        "erittely",
        "listaus",
        "luettelo",
        "yksityiskohta",
        "spesifikaatio",
        "määrittely")) {
      return SPECIFICATION;
    }

    return UNKNOWN;
  }

  private static boolean containsAny(String text, String... keywords) {
    for (String keyword : keywords) {
      if (text.contains(keyword)) {
        return true;
      }
    }
    return false;
  }
}
