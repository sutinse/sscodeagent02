# PDF Text Extractor - Quarkus Application

A Quarkus application that extracts raw text data from PDF files through a REST API with multipart file upload support.

## Features

- **Dual extraction methods**: Primary extraction with Apache PDFBox, fallback to TesseractOCR
- **Finnish language support**: OCR configured for Finnish text recognition
- **Document type detection**: Automatic detection of common document types (invoices, receipts, contracts, etc.)
- **Text normalization**: Finnish-specific text normalization for OCR results
- **Modern Java**: Uses Java records and modern language features
- **REST API**: Clean REST endpoints for file upload and health checking

## Requirements

- Java 17+
- Maven 3.6+
- TesseractOCR (for OCR functionality)

## Quick Start

### Build and Run

```bash
# Build the application
mvn clean compile

# Run in development mode
mvn quarkus:dev

# Run tests
mvn test
```

### API Endpoints

#### Health Check
```
GET /api/pdf/health
```

Response:
```json
{
  "service": "PDF Extractor Service",
  "status": "UP"
}
```

#### PDF Text Extraction
```
POST /api/pdf/extract
Content-Type: multipart/form-data
```

**Request**: Upload a PDF file using form field name `file`

**Response**:
```json
{
  "extractedText": "Text content from PDF...",
  "methodUsed": "PDFBOX",
  "documentType": "INVOICE",
  "success": true,
  "errorMessage": null,
  "metadata": {
    "extractionTime": "2023-08-31T09:00:00",
    "processingTimeMs": 1234,
    "pageCount": 2,
    "originalFilename": "invoice.pdf",
    "fileSizeBytes": 45678,
    "textNormalized": false,
    "language": "fi"
  }
}
```

### Using curl

```bash
# Health check
curl http://localhost:8080/api/pdf/health

# Extract text from PDF
curl -X POST -F "file=@your-document.pdf" http://localhost:8080/api/pdf/extract
```

## Document Types

The application automatically detects the following document types:

- `INVOICE` (Lasku) - Invoices and bills
- `RECEIPT` (Kuitti) - Shopping receipts
- `CONTRACT` (Sopimus) - Contracts and agreements
- `CERTIFICATE` (Todistus) - Certificates and diplomas
- `REPORT` (Raportti) - Reports and analyses
- `FORM` (Lomake) - Forms and applications
- `LETTER` (Kirje) - Letters and correspondence
- `MANUAL` (Käyttöohje) - User manuals and guides
- `SPECIFICATION` (Erittely) - Specifications and listings
- `UNKNOWN` (Tuntematon) - Unknown or unrecognized documents

## Extraction Methods

1. **PDFBox** (`PDFBOX`): Primary method for text-based PDFs
2. **TesseractOCR** (`TESSERACT_OCR`): Fallback method for image-based PDFs
3. **Hybrid** (`HYBRID`): Future enhancement for combined approaches

## Configuration

Key configuration properties in `application.properties`:

```properties
# HTTP and file upload
quarkus.http.port=8080
quarkus.http.limits.max-body-size=50M

# TesseractOCR settings
tesseract.language=fin
tesseract.dpi=300
tesseract.ocr.engine.mode=1
tesseract.page.seg.mode=1
```

## Architecture

### Core Components

- **PdfExtractionService**: Main service orchestrating PDF text extraction
- **TesseractOcrService**: OCR service using TesseractOCR
- **TextNormalizationService**: Finnish text normalization and cleaning
- **DocumentType**: Enum with document type detection logic
- **PdfExtractionResource**: REST API controller

### Data Structures

Modern Java records are used for clean, immutable data structures:

- `PdfExtractionResponse`: Main API response record
- `ExtractionMetadata`: Processing metadata record
- `ExtractionMethod`: Enum for extraction methods

## Dependencies

- **Quarkus**: Framework for cloud-native Java applications
- **PDFBox 3.0.3**: Primary PDF text extraction library
- **Tess4J 5.13.0**: Java wrapper for TesseractOCR
- **Jakarta EE**: Enterprise Java APIs
- **JUnit 5**: Testing framework

## Development

### Project Structure

```
src/
├── main/java/fi/sutinse/pdfextractor/
│   ├── dto/           # Data Transfer Objects (Records)
│   ├── model/         # Domain models and enums
│   ├── resource/      # REST controllers
│   └── service/       # Business logic services
├── main/resources/
│   └── application.properties
└── test/java/         # Test classes
```

### Testing

The application includes comprehensive tests:

- Unit tests for services
- Integration tests for REST endpoints
- Error handling validation

Run tests with: `mvn test`

## License

This project is part of the sscodeagent02 repository.