package fi.sutinse.pdfextractor.service;

import static org.junit.jupiter.api.Assertions.*;

import fi.sutinse.pdfextractor.model.DocumentType;
import fi.sutinse.pdfextractor.model.Language;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StructuredDataExtractionServiceTest {

  private StructuredDataExtractionService service;

  @BeforeEach
  void setUp() {
    service = new StructuredDataExtractionService();
  }

  @Test
  void testExtractStructuredDataFromInvoiceTextFinnish() {
    String text = """
        Lasku numero: 12345
        Eräpäivä: 31.12.2024
        Yhteensä: 100,00 €
        Asiakas: Testi Asiaka
        """;

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.FINNISH);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    
    // Verify extracted invoice number
    assertEquals("12345", result.get("invoice_number"));
    
    // Verify extracted due date
    assertEquals("31.12.2024", result.get("due_date"));
    
    // Verify extracted total amount
    assertEquals("100,00 €", result.get("total_amount"));
    
    // Verify extracted customer name
    assertEquals("Testi Asiaka", result.get("customer_name"));
  }

  @Test
  void testExtractStructuredDataFromInvoiceTextSwedish() {
    String text = """
        Faktura nummer: 54321
        Förfallodag: 2024-12-31
        Totalt: 200,50 kr
        Kund: Test Kund
        """;

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.SWEDISH);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    
    assertEquals("54321", result.get("invoice_number"));
    assertEquals("2024-12-31", result.get("due_date"));
    assertEquals("200,50 kr", result.get("total_amount"));
    assertEquals("Test Kund", result.get("customer_name"));
  }

  @Test
  void testExtractStructuredDataFromInvoiceTextEnglish() {
    String text = """
        Invoice Number: 98765
        Due Date: December 31, 2024
        Total: $150.75
        Customer: Test Customer
        """;

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.ENGLISH);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    
    assertEquals("98765", result.get("invoice_number"));
    assertEquals("December 31, 2024", result.get("due_date"));
    assertEquals("$150.75", result.get("total_amount"));
    assertEquals("Test Customer", result.get("customer_name"));
  }

  @Test
  void testExtractStructuredDataFromReceiptText() {
    String text = """
        Kuitti numero: R-001
        Päivämäärä: 15.11.2024
        Yhteensä: 45,20 €
        Kauppa: Test Market
        """;

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.RECEIPT, Language.FINNISH);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    
    assertEquals("R-001", result.get("receipt_number"));
    assertEquals("15.11.2024", result.get("date"));
    assertEquals("45,20 €", result.get("total_amount"));
    assertEquals("Test Market", result.get("store_name"));
  }

  @Test
  void testExtractStructuredDataFromContractText() {
    String text = """
        Sopimus numero: C-2024-001
        Osapuolet: Yritys A ja Yritys B
        Voimaantulo: 1.1.2025
        """;

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.CONTRACT, Language.FINNISH);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    
    assertEquals("C-2024-001", result.get("contract_number"));
    assertEquals("Yritys A ja Yritys B", result.get("parties"));
    assertEquals("1.1.2025", result.get("effective_date"));
  }

  @Test
  void testExtractStructuredDataWithColonSeparator() {
    String text = "Lasku numero: 12345";

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.FINNISH);

    assertEquals("12345", result.get("invoice_number"));
  }

  @Test
  void testExtractStructuredDataWithoutColon() {
    String text = "Lasku numero 67890";

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.FINNISH);

    assertEquals("67890", result.get("invoice_number"));
  }

  @Test
  void testExtractStructuredDataWithCurrencyPattern() {
    String text = "Yhteensä: 199,99 €";

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.FINNISH);

    assertEquals("199,99 €", result.get("total_amount"));
  }

  @Test
  void testExtractStructuredDataWithDatePattern() {
    String text = "Eräpäivä: 25.12.2024";

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.FINNISH);

    assertEquals("25.12.2024", result.get("due_date"));
  }

  @Test
  void testExtractStructuredDataEmptyText() {
    Map<String, Object> result = 
        service.extractStructuredData("", DocumentType.INVOICE, Language.FINNISH);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testExtractStructuredDataNullText() {
    Map<String, Object> result = 
        service.extractStructuredData(null, DocumentType.INVOICE, Language.FINNISH);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testExtractStructuredDataNoMatches() {
    String text = "This is random text with no recognizable fields.";

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.FINNISH);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testExtractStructuredDataMultipleLanguagePatterns() {
    // When language is detected as Finnish but text is in English, should still work
    String text = "Invoice Number: 12345";

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.INVOICE, Language.FINNISH);

    // Should find the pattern even if language detection was wrong
    assertEquals("12345", result.get("invoice_number"));
  }

  @Test
  void testExtractStructuredDataCertificate() {
    String text = """
        Todistus numero: CERT-2024-001
        Myönnetty: Testi Henkilö
        Myöntämispäivä: 1.12.2024
        """;

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.CERTIFICATE, Language.FINNISH);

    assertNotNull(result);
    assertEquals("CERT-2024-001", result.get("certificate_number"));
    assertEquals("Testi Henkilö", result.get("issued_to"));
    assertEquals("1.12.2024", result.get("issue_date"));
  }

  @Test
  void testExtractStructuredDataSpecification() {
    String text = """
        Erittelyn nimi: Product Specification
        Kohde numero: PROD-001
        Määrä: 50 kpl
        """;

    Map<String, Object> result = 
        service.extractStructuredData(text, DocumentType.SPECIFICATION, Language.FINNISH);

    assertNotNull(result);
    assertEquals("Product Specification", result.get("specification_title"));
    assertEquals("PROD-001", result.get("item_number"));
    assertEquals("50 kpl", result.get("quantity"));
  }
}