# PDF Text Extractor - Multi-Language Support and Docker

## Overview

This application has been enhanced to support multiple languages (Finnish, Swedish, and English) with automatic language detection capabilities and Docker deployment options.

## New Features

### Multi-Language Support

The application now supports:
- **Finnish** (`fin`) - Original support maintained
- **Swedish** (`swe`) - New support added
- **English** (`eng`) - New support added

### Language Detection

The application can automatically detect the language of PDF documents by analyzing:
- Language-specific words and phrases
- Character patterns (ä, ö, å for Nordic languages)
- Common document terminology

### Configurable Language Settings

Language behavior can be configured through application properties:

```properties
# Default language (fin, swe, eng)
tesseract.language=fin

# Enable/disable automatic language detection
tesseract.auto-detect-language=true
```

### Enhanced Document Type Detection

Document type detection now works for all supported languages:
- **Invoice** - Lasku (FI), Faktura (SE), Invoice (EN)
- **Receipt** - Kuitti (FI), Kvitto (SE), Receipt (EN)
- **Contract** - Sopimus (FI), Kontrakt (SE), Contract (EN)
- And more...

### Text Normalization

Text normalization now includes language-specific corrections:
- Finnish: Special character handling (ä, ö, å), common OCR errors
- Swedish: Similar Nordic character support, Swedish-specific patterns
- English: English text patterns and common OCR corrections

## Docker Support

### JVM Docker Image

Build and run the JVM version:

```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -f Dockerfile.jvm -t pdf-extractor-jvm:latest .

# Run the container
docker run -p 8080:8080 \
  -e TESSERACT_LANGUAGE=fin \
  -e TESSERACT_AUTO_DETECT_LANGUAGE=true \
  pdf-extractor-jvm:latest
```

### Native Docker Image

Build and run the native version:

```bash
# Build Docker image (includes native compilation)
docker build -f Dockerfile.native -t pdf-extractor-native:latest .

# Run the container
docker run -p 8080:8080 \
  -e TESSERACT_LANGUAGE=fin \
  -e TESSERACT_AUTO_DETECT_LANGUAGE=true \
  pdf-extractor-native:latest
```

### Docker Compose

Use the provided docker-compose.yml to run both versions:

```bash
# Run JVM version
docker-compose up pdf-extractor-jvm

# Run native version
docker-compose up pdf-extractor-native

# Run both
docker-compose up
```

Services will be available at:
- JVM version: http://localhost:8080
- Native version: http://localhost:8081

## Environment Variables

Configure the application using environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `TESSERACT_LANGUAGE` | `fin` | Default language for OCR (fin, swe, eng) |
| `TESSERACT_AUTO_DETECT_LANGUAGE` | `true` | Enable automatic language detection |
| `QUARKUS_LOG_LEVEL` | `INFO` | Application log level |

## TesseractOCR Installation in Docker

Both Docker images include:
- TesseractOCR 5.3.4+
- Finnish language pack (`tesseract-ocr-fin`)
- Swedish language pack (`tesseract-ocr-swe`)
- English language pack (`tesseract-ocr-eng`)

## Testing Multi-Language Support

### Create Test Documents

For manual testing, you can create simple test PDFs:

```bash
# Finnish test PDF
cat > test_finnish.ps << 'EOF'
%!PS-Adobe-3.0
/Helvetica findfont 12 scalefont setfont
72 720 moveto
(Lasku numero: 12345) show
72 700 moveto
(Eräpäivä: 31.12.2024) show
72 680 moveto
(Yhteensä: 100,00 €) show
showpage
EOF
ps2pdf test_finnish.ps test_finnish.pdf

# Swedish test PDF
cat > test_swedish.ps << 'EOF'
%!PS-Adobe-3.0
/Helvetica findfont 12 scalefont setfont
72 720 moveto
(Faktura nummer: 12345) show
72 700 moveto
(Förfallodag: 31.12.2024) show
72 680 moveto
(Totalt: 100,00 kr) show
showpage
EOF
ps2pdf test_swedish.ps test_swedish.pdf

# English test PDF
cat > test_english.ps << 'EOF'
%!PS-Adobe-3.0
/Helvetica findfont 12 scalefont setfont
72 720 moveto
(Invoice Number: 12345) show
72 700 moveto
(Due Date: 12/31/2024) show
72 680 moveto
(Total: $100.00) show
showpage
EOF
ps2pdf test_english.ps test_english.pdf
```

### Test Language Detection

```bash
# Test Finnish document
curl -X POST -F "file=@test_finnish.pdf" http://localhost:8080/api/pdf/extract

# Test Swedish document  
curl -X POST -F "file=@test_swedish.pdf" http://localhost:8080/api/pdf/extract

# Test English document
curl -X POST -F "file=@test_english.pdf" http://localhost:8080/api/pdf/extract
```

The response will include detected language and document type:

```json
{
  "success": true,
  "extractedText": "Lasku numero: 12345...",
  "method": "PDFBOX",
  "documentType": "INVOICE",
  "metadata": {
    "language": "fin",
    "processingTimeMs": 150
  }
}
```

## API Changes

The API responses now include language information in the metadata:

```json
{
  "success": true,
  "extractedText": "Document text...",
  "method": "PDFBOX|TESSERACT_OCR",
  "documentType": "INVOICE|RECEIPT|CONTRACT|...",
  "metadata": {
    "extractionTime": "2024-01-01T12:00:00",
    "processingTimeMs": 1500,
    "pageCount": 2,
    "originalFilename": "document.pdf",
    "fileSizeBytes": 25600,
    "textNormalized": true,
    "language": "fin|swe|eng"
  }
}
```

## Performance Notes

- **Language Detection**: Adds minimal overhead (~1-5ms)
- **Auto-detection**: May require processing first page twice for OCR
- **Native Image**: Faster startup (~1-2s vs ~10-15s for JVM)
- **JVM Image**: Better for development with hot reload support

## Troubleshooting

### Language Detection Issues

If language detection is not working correctly:

1. Disable auto-detection: `TESSERACT_AUTO_DETECT_LANGUAGE=false`
2. Set explicit language: `TESSERACT_LANGUAGE=fin`
3. Check TesseractOCR language packs: `tesseract --list-langs`

### Docker Build Issues

If Docker builds fail:

1. Ensure network connectivity to package repositories
2. Try building with `--no-cache` flag
3. Use alternative base images if repository access is limited

### Performance Optimization

For production use:
- Use native image for faster startup
- Consider disabling auto-detection for known document languages
- Implement caching for frequently processed documents

## Compatibility

- Java 21+
- Maven 3.6+
- Docker 20.10+
- TesseractOCR 5.3.4+

## Migration from Single-Language Version

Existing installations will continue to work with Finnish as the default language. To enable multi-language support:

1. Update application.properties to enable auto-detection
2. Rebuild and redeploy the application
3. Test with documents in different languages

The API remains backward compatible - existing clients will continue to work without changes.