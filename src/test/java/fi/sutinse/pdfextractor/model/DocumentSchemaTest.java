package fi.sutinse.pdfextractor.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DocumentSchemaTest {

  @Test
  void testGetSchemaForInvoiceDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.INVOICE);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    // Verify invoice number field
    assertTrue(schema.containsKey("invoice_number"));
    DocumentSchema.FieldSchema invoiceNumberField = schema.get("invoice_number");
    assertEquals("Invoice Number", invoiceNumberField.englishName());
    assertTrue(invoiceNumberField.finnishPatterns().contains("lasku numero"));
    assertTrue(invoiceNumberField.swedishPatterns().contains("faktura nummer"));
    assertTrue(invoiceNumberField.englishPatterns().contains("invoice number"));

    // Verify due date field
    assertTrue(schema.containsKey("due_date"));
    DocumentSchema.FieldSchema dueDateField = schema.get("due_date");
    assertEquals("Due Date", dueDateField.englishName());
    assertTrue(dueDateField.finnishPatterns().contains("eräpäivä"));

    // Verify total amount field
    assertTrue(schema.containsKey("total_amount"));
    DocumentSchema.FieldSchema totalAmountField = schema.get("total_amount");
    assertEquals("Total Amount", totalAmountField.englishName());
    assertTrue(totalAmountField.finnishPatterns().contains("yhteensä"));
  }

  @Test
  void testGetSchemaForReceiptDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.RECEIPT);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("receipt_number"));
    assertTrue(schema.containsKey("date"));
    assertTrue(schema.containsKey("total_amount"));
    assertTrue(schema.containsKey("store_name"));

    DocumentSchema.FieldSchema receiptNumberField = schema.get("receipt_number");
    assertEquals("Receipt Number", receiptNumberField.englishName());
    assertTrue(receiptNumberField.finnishPatterns().contains("kuitti numero"));
  }

  @Test
  void testGetSchemaForContractDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.CONTRACT);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("contract_number"));
    assertTrue(schema.containsKey("parties"));
    assertTrue(schema.containsKey("effective_date"));

    DocumentSchema.FieldSchema contractNumberField = schema.get("contract_number");
    assertEquals("Contract Number", contractNumberField.englishName());
    assertTrue(contractNumberField.finnishPatterns().contains("sopimus numero"));
  }

  @Test
  void testGetSchemaForCertificateDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.CERTIFICATE);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("certificate_number"));
    assertTrue(schema.containsKey("issued_to"));
    assertTrue(schema.containsKey("issue_date"));
  }

  @Test
  void testGetSchemaForReportDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.REPORT);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("report_title"));
    assertTrue(schema.containsKey("date"));
    assertTrue(schema.containsKey("author"));
  }

  @Test
  void testGetSchemaForFormDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.FORM);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("form_number"));
    assertTrue(schema.containsKey("applicant_name"));
    assertTrue(schema.containsKey("date"));
  }

  @Test
  void testGetSchemaForLetterDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.LETTER);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("sender"));
    assertTrue(schema.containsKey("recipient"));
    assertTrue(schema.containsKey("date"));
    assertTrue(schema.containsKey("subject"));
  }

  @Test
  void testGetSchemaForManualDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.MANUAL);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("product_name"));
    assertTrue(schema.containsKey("version"));
    assertTrue(schema.containsKey("manual_date"));
  }

  @Test
  void testGetSchemaForSpecificationDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.SPECIFICATION);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("specification_title"));
    assertTrue(schema.containsKey("item_number"));
    assertTrue(schema.containsKey("quantity"));
  }

  @Test
  void testGetSchemaForUnknownDocumentType() {
    Map<String, DocumentSchema.FieldSchema> schema = 
        DocumentSchema.getSchemaForDocumentType(DocumentType.UNKNOWN);

    assertNotNull(schema);
    assertFalse(schema.isEmpty());

    assertTrue(schema.containsKey("document_title"));
    assertTrue(schema.containsKey("date"));
    assertTrue(schema.containsKey("reference_number"));
  }

  @Test
  void testFieldSchemaRecord() {
    DocumentSchema.FieldSchema field = new DocumentSchema.FieldSchema(
        "Test Field",
        java.util.List.of("testi kenttä"),
        java.util.List.of("test fält"),
        java.util.List.of("test field")
    );

    assertEquals("Test Field", field.englishName());
    assertEquals(1, field.finnishPatterns().size());
    assertEquals("testi kenttä", field.finnishPatterns().get(0));
    assertEquals(1, field.swedishPatterns().size());
    assertEquals("test fält", field.swedishPatterns().get(0));
    assertEquals(1, field.englishPatterns().size());
    assertEquals("test field", field.englishPatterns().get(0));
  }
}