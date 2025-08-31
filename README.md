# PDF Text Extractor - Quarkus Application

A Quarkus application that extracts raw text data from PDF files through a REST API with multipart file upload support.

## Features

- **Dual extraction methods**: Primary extraction with Apache PDFBox, fallback to TesseractOCR
- **Multi-language support**: Automatic language detection for Finnish, Swedish, and English
- **Document type detection**: Automatic detection of common document types (invoices, receipts, contracts, etc.)
- **Language-specific text normalization**: Text normalization optimized for each supported language
- **Modern Java**: Uses Java records and modern language features
- **REST API**: Clean REST endpoints for file upload and health checking
- **Docker support**: Both JVM and native Docker images available

## Supported Languages

- **Finnish** (`fin`) - Original support with comprehensive text patterns
- **Swedish** (`swe`) - Full support with Nordic character handling
- **English** (`eng`) - Complete support with English document patterns

The application automatically detects the document language by analyzing text content and applies appropriate language-specific processing.

## Requirements

- Java 17+
- Maven 3.6+
- TesseractOCR with language packs (fin, swe, eng)

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
    "language": "fin"
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

- `INVOICE` - Invoices and bills (Lasku/Faktura/Invoice)
- `RECEIPT` - Shopping receipts (Kuitti/Kvitto/Receipt)
- `CONTRACT` - Contracts and agreements (Sopimus/Kontrakt/Contract)
- `CERTIFICATE` - Certificates and diplomas (Todistus/Certifikat/Certificate)
- `REPORT` - Reports and analyses (Raportti/Rapport/Report)
- `FORM` - Forms and applications (Lomake/Blankett/Form)
- `LETTER` - Letters and correspondence (Kirje/Brev/Letter)
- `MANUAL` - User manuals and guides (Käyttöohje/Manual/Manual)
- `SPECIFICATION` - Specifications and listings (Erittely/Specifikation/Specification)
- `UNKNOWN` - Unknown or unrecognized documents (Tuntematon/Okänd/Unknown)

Document types are detected using language-specific keywords and patterns for all supported languages.

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
tesseract.language=fin                    # Default language (fin, swe, eng)
tesseract.auto-detect-language=true      # Enable automatic language detection
tesseract.dpi=300
tesseract.ocr.engine.mode=1
tesseract.page.seg.mode=1
```

## Docker Support

The application includes Docker support for both JVM and native builds:

```bash
# JVM Docker build
docker build -f Dockerfile.jvm -t pdf-extractor-jvm .

# Native Docker build  
docker build -f Dockerfile.native -t pdf-extractor-native .

# Using Docker Compose
docker-compose up pdf-extractor-jvm    # JVM on port 8080
docker-compose up pdf-extractor-native # Native on port 8081
```

See [DOCKER_MULTI_LANGUAGE.md](DOCKER_MULTI_LANGUAGE.md) for comprehensive Docker documentation.

## Architecture

### Core Components

- **PdfExtractionService**: Main service orchestrating PDF text extraction
- **TesseractOcrService**: OCR service with multi-language support
- **TextNormalizationService**: Language-specific text normalization and cleaning
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

### Multi-Language Testing

To test the multi-language functionality:

```bash
# Create test PDFs for different languages
make test-multilang

# Test with different language documents
curl -X POST -F "file=@test-docs/test_finnish.pdf" http://localhost:8080/api/pdf/extract
curl -X POST -F "file=@test-docs/test_swedish.pdf" http://localhost:8080/api/pdf/extract  
curl -X POST -F "file=@test-docs/test_english.pdf" http://localhost:8080/api/pdf/extract
```

The response will include the detected language in the metadata:
```json
{
  "success": true,
  "extractedText": "Lasku numero: 12345...",
  "documentType": "INVOICE",
  "metadata": {
    "language": "fin"
  }
}
```

## License

This project is part of the sscodeagent02 repository.