package fi.sutinse.pdfextractor.model;

import java.util.List;
import java.util.Map;

/** JSON schema definitions for structured data extraction by document type */
public class DocumentSchema {

  /** Schema field definition */
  public record FieldSchema(
      String englishName,
      List<String> finnishPatterns,
      List<String> swedishPatterns,
      List<String> englishPatterns) {}

  /** Get schema for document type */
  public static Map<String, FieldSchema> getSchemaForDocumentType(DocumentType documentType) {
    return switch (documentType) {
      case INVOICE -> getInvoiceSchema();
      case RECEIPT -> getReceiptSchema();
      case CONTRACT -> getContractSchema();
      case CERTIFICATE -> getCertificateSchema();
      case REPORT -> getReportSchema();
      case FORM -> getFormSchema();
      case LETTER -> getLetterSchema();
      case MANUAL -> getManualSchema();
      case SPECIFICATION -> getSpecificationSchema();
      case UNKNOWN -> getGenericSchema();
    };
  }

  private static Map<String, FieldSchema> getInvoiceSchema() {
    return Map.of(
        "invoice_number",
            new FieldSchema(
                "Invoice Number",
                List.of("lasku numero", "laskun numero", "lasku nro", "lasku", "numero"),
                List.of("faktura nummer", "fakturanummer", "faktura nr", "nummer"),
                List.of("invoice number", "invoice no", "invoice #", "inv no", "number")),
        "due_date",
            new FieldSchema(
                "Due Date",
                List.of("eräpäivä", "erääntymispäivä", "maksupäivä", "maksu mennessä"),
                List.of("förfallodag", "förfallodatum", "betalningsdag", "betala senast"),
                List.of("due date", "due", "payment due", "pay by")),
        "total_amount",
            new FieldSchema(
                "Total Amount",
                List.of("yhteensä", "summa", "kokonaissumma", "loppusumma"),
                List.of("totalt", "summa", "totalsumma", "slutsumma"),
                List.of("total", "amount", "total amount", "sum")),
        "vat_amount",
            new FieldSchema(
                "VAT Amount",
                List.of("alv", "arvonlisävero", "vero"),
                List.of("moms", "mervärdesskatt", "skatt"),
                List.of("vat", "tax", "value added tax")),
        "customer_name",
            new FieldSchema(
                "Customer Name",
                List.of("asiakas", "laskutettava", "maksaja", "nimi"),
                List.of("kund", "faktureras", "betalare", "namn"),
                List.of("customer", "client", "bill to", "name")));
  }

  private static Map<String, FieldSchema> getReceiptSchema() {
    return Map.of(
        "receipt_number",
            new FieldSchema(
                "Receipt Number",
                List.of("kuitti numero", "kuitin numero", "kuitti nro", "numero"),
                List.of("kvitto nummer", "kvittonummer", "kvitto nr", "nummer"),
                List.of("receipt number", "receipt no", "receipt #", "number")),
        "date",
            new FieldSchema(
                "Date",
                List.of("päivämäärä", "pvm", "aika", "päivä"),
                List.of("datum", "dag", "tid"),
                List.of("date", "time", "day")),
        "total_amount",
            new FieldSchema(
                "Total Amount",
                List.of("yhteensä", "summa", "kokonaissumma"),
                List.of("totalt", "summa", "totalsumma"),
                List.of("total", "amount", "sum")),
        "store_name",
            new FieldSchema(
                "Store Name",
                List.of("kauppa", "myymälä", "liike"),
                List.of("butik", "affär", "handel"),
                List.of("store", "shop", "merchant")));
  }

  private static Map<String, FieldSchema> getContractSchema() {
    return Map.of(
        "contract_number",
            new FieldSchema(
                "Contract Number",
                List.of("sopimus numero", "sopimuksen numero", "sopimus nro", "numero"),
                List.of("kontrakt nummer", "kontraktnummer", "kontrakt nr", "nummer"),
                List.of("contract number", "contract no", "contract #", "agreement no")),
        "parties",
            new FieldSchema(
                "Parties",
                List.of("osapuolet", "sopijapuolet", "sopimuspuolet"),
                List.of("parter", "avtalspart", "kontraktspart"),
                List.of("parties", "contracting parties", "signatories")),
        "effective_date",
            new FieldSchema(
                "Effective Date",
                List.of("voimaantulo", "voimassa", "alkamispäivä"),
                List.of("ikraftträdande", "giltighet", "startdatum"),
                List.of("effective date", "valid from", "start date")));
  }

  private static Map<String, FieldSchema> getCertificateSchema() {
    return Map.of(
        "certificate_number",
            new FieldSchema(
                "Certificate Number",
                List.of("todistus numero", "todistuksen numero", "sertifikaatti", "numero"),
                List.of("certifikat nummer", "certifikatnummer", "nummer"),
                List.of("certificate number", "cert no", "certificate #", "number")),
        "issued_to",
            new FieldSchema(
                "Issued To",
                List.of("myönnetty", "annettu", "vastaanottaja", "henkilö"),
                List.of("utfärdat till", "mottagare", "person"),
                List.of("issued to", "awarded to", "recipient", "name")),
        "issue_date",
            new FieldSchema(
                "Issue Date",
                List.of("myöntämispäivä", "antopäivä", "päivämäärä"),
                List.of("utfärdandedatum", "datum"),
                List.of("issue date", "issued", "date")));
  }

  private static Map<String, FieldSchema> getReportSchema() {
    return Map.of(
        "report_title",
            new FieldSchema(
                "Report Title",
                List.of("raportin otsikko", "otsikko", "nimi", "aihe"),
                List.of("rapportens titel", "titel", "namn", "ämne"),
                List.of("report title", "title", "subject", "name")),
        "date",
            new FieldSchema(
                "Date",
                List.of("päivämäärä", "pvm", "luotu", "päivä"),
                List.of("datum", "skapad", "dag"),
                List.of("date", "created", "report date")),
        "author",
            new FieldSchema(
                "Author",
                List.of("tekijä", "kirjoittaja", "laatija"),
                List.of("författare", "skribent", "upphovsman"),
                List.of("author", "prepared by", "written by")));
  }

  private static Map<String, FieldSchema> getFormSchema() {
    return Map.of(
        "form_number",
            new FieldSchema(
                "Form Number",
                List.of("lomake numero", "lomakkeen numero", "lomake nro", "numero"),
                List.of("blankett nummer", "blankettnummer", "blankett nr", "nummer"),
                List.of("form number", "form no", "form #", "number")),
        "applicant_name",
            new FieldSchema(
                "Applicant Name",
                List.of("hakijan nimi", "hakija", "nimi", "henkilö"),
                List.of("sökandes namn", "sökande", "namn", "person"),
                List.of("applicant name", "applicant", "name", "person")),
        "date",
            new FieldSchema(
                "Date",
                List.of("päivämäärä", "pvm", "täytetty", "päivä"),
                List.of("datum", "ifylld", "dag"),
                List.of("date", "filled", "application date")));
  }

  private static Map<String, FieldSchema> getLetterSchema() {
    return Map.of(
        "sender",
            new FieldSchema(
                "Sender",
                List.of("lähettäjä", "kiireellinen", "kuittaamatto"),
                List.of("avsändare", "från"),
                List.of("sender", "from")),
        "recipient",
            new FieldSchema(
                "Recipient",
                List.of("vastaanottaja", "kenelle", "kohde"),
                List.of("mottagare", "till"),
                List.of("recipient", "to")),
        "date",
            new FieldSchema(
                "Date",
                List.of("päivämäärä", "pvm", "päivä"),
                List.of("datum", "dag"),
                List.of("date", "dated")),
        "subject",
            new FieldSchema(
                "Subject",
                List.of("aihe", "otsikko", "asia", "koskien"),
                List.of("ämne", "angående", "beträffande"),
                List.of("subject", "re", "regarding")));
  }

  private static Map<String, FieldSchema> getManualSchema() {
    return Map.of(
        "product_name",
            new FieldSchema(
                "Product Name",
                List.of("tuotteen nimi", "tuote", "laite", "malli"),
                List.of("produktnamn", "produkt", "enhet", "modell"),
                List.of("product name", "product", "device", "model")),
        "version",
            new FieldSchema(
                "Version",
                List.of("versio", "v", "ver", "painos"),
                List.of("version", "v", "ver", "utgåva"),
                List.of("version", "v", "ver", "edition")),
        "manual_date",
            new FieldSchema(
                "Manual Date",
                List.of("päivämäärä", "pvm", "julkaistu"),
                List.of("datum", "publicerad"),
                List.of("date", "published", "manual date")));
  }

  private static Map<String, FieldSchema> getSpecificationSchema() {
    return Map.of(
        "specification_title",
            new FieldSchema(
                "Specification Title",
                List.of("erittelyn nimi", "otsikko", "nimi", "aihe"),
                List.of("specifikationsnamn", "titel", "namn", "ämne"),
                List.of("specification title", "title", "name", "subject")),
        "item_number",
            new FieldSchema(
                "Item Number",
                List.of("kohde numero", "tuote numero", "numero", "koodi"),
                List.of("artikelnummer", "produktnummer", "nummer", "kod"),
                List.of("item number", "product number", "part number", "code")),
        "quantity",
            new FieldSchema(
                "Quantity",
                List.of("määrä", "kpl", "kappaletta", "lukumäärä"),
                List.of("antal", "st", "stycken", "kvantitet"),
                List.of("quantity", "qty", "amount", "pieces")));
  }

  private static Map<String, FieldSchema> getGenericSchema() {
    return Map.of(
        "document_title",
            new FieldSchema(
                "Document Title",
                List.of("otsikko", "nimi", "aihe"),
                List.of("titel", "namn", "ämne"),
                List.of("title", "name", "subject")),
        "date",
            new FieldSchema(
                "Date",
                List.of("päivämäärä", "pvm", "päivä"),
                List.of("datum", "dag"),
                List.of("date")),
        "reference_number",
            new FieldSchema(
                "Reference Number",
                List.of("viite numero", "viite", "ref", "numero"),
                List.of("referensnummer", "referens", "ref", "nummer"),
                List.of("reference number", "reference", "ref", "number")));
  }
}