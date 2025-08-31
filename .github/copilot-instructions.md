# PDF Text Extractor - Quarkus Application

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Bootstrap, Build, and Test the Repository:
- **Install TesseractOCR (REQUIRED)**: `sudo apt update && sudo apt install -y tesseract-ocr tesseract-ocr-fin`
- **Install Ghostscript (for testing)**: `sudo apt install -y ghostscript`
- **Verify Java 17+**: `java -version` (OpenJDK 17+ required)
- **Verify Maven 3.6+**: `mvn -version` (Maven 3.9+ available)
- **Clean compile**: `mvn clean compile` -- takes 4-7 seconds. NEVER CANCEL. Set timeout to 30+ seconds.
- **Full build with tests**: `mvn clean package` -- takes 20-25 seconds. NEVER CANCEL. Set timeout to 60+ seconds.
- **Run tests only**: `mvn test` -- takes 8-12 seconds. NEVER CANCEL. Set timeout to 30+ seconds.
- **Build without tests**: `mvn clean package -DskipTests` -- takes 6-8 seconds. NEVER CANCEL. Set timeout to 30+ seconds.

### Run the Application:
- **ALWAYS build first**: Complete bootstrapping steps above before running.
- **Development mode**: `mvn quarkus:dev` -- starts in 1-2 seconds, provides live reload
  - Access at: http://localhost:8080
  - Press 'q' to quit
- **Packaged application**: `java -jar target/quarkus-app/quarkus-run.jar`
  - Must run `mvn clean package` first
  - Access at: http://localhost:8080

### Code Quality and Formatting:
- **Format code**: `mvn com.spotify.fmt:fmt-maven-plugin:format` -- applies Google Java Format. Takes 3-5 seconds.
- **Check formatting**: `mvn com.spotify.fmt:fmt-maven-plugin:check` -- validates code formatting. Takes 3-5 seconds.
- **ALWAYS run formatting check before committing**: The formatting check will fail if code is not properly formatted.

## Validation

### Manual End-to-End Testing:
- **ALWAYS test complete PDF extraction workflow** after making changes:
  1. Start application: `mvn quarkus:dev`
  2. Health check: `curl http://localhost:8080/api/pdf/health`
  3. Create test PDF: 
     ```bash
     cd /tmp && cat > test.ps << 'EOF'
     %!PS-Adobe-3.0
     /Helvetica findfont 12 scalefont setfont
     72 720 moveto
     (This is a test document.) show
     72 700 moveto
     (Tämä on testi-dokumentti.) show
     showpage
     EOF
     ps2pdf test.ps test.pdf
     ```
  4. Test extraction: `curl -X POST -F "file=@test.pdf" http://localhost:8080/api/pdf/extract`
  5. Verify response contains `"success":true` and `"extractedText"` with content
  6. Test error handling: `echo "invalid" > invalid.pdf && curl -X POST -F "file=@invalid.pdf" http://localhost:8080/api/pdf/extract`
  7. Verify error response contains `"success":false` and proper error message

### TesseractOCR Validation:
- **Verify TesseractOCR installation**: `tesseract --version` (should show 5.3.4+)
- **Verify Finnish language support**: `tesseract --list-langs` (should include 'fin')
- **OCR functionality**: Application uses PDFBox first, TesseractOCR as fallback for image-based PDFs

### Required Validation Steps:
- **ALWAYS run tests**: `mvn test` before committing changes
- **ALWAYS check formatting**: `mvn com.spotify.fmt:fmt-maven-plugin:check` before committing
- **ALWAYS test health endpoint**: `curl http://localhost:8080/api/pdf/health` after starting application
- **ALWAYS test PDF extraction with valid and invalid files** (see end-to-end testing above)

## Architecture and Key Components

### Core Components:
- **PdfExtractionService**: Main service orchestrating PDF text extraction
- **TesseractOcrService**: OCR service using TesseractOCR with Finnish language support
- **TextNormalizationService**: Finnish text normalization and cleaning
- **DocumentType**: Enum with document type detection logic (INVOICE, RECEIPT, CONTRACT, etc.)
- **PdfExtractionResource**: REST API controller at `/api/pdf/*`

### Project Structure:
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

### API Endpoints:
- **Health**: `GET /api/pdf/health`
- **Extract**: `POST /api/pdf/extract` (multipart/form-data with "file" field)

### Dependencies:
- **Quarkus 3.9.5**: Framework for cloud-native Java applications
- **PDFBox 3.0.3**: Primary PDF text extraction library
- **Tess4J 5.13.0**: Java wrapper for TesseractOCR
- **Java 17+**: Required runtime
- **Maven 3.6+**: Build system

## Common Tasks and Troubleshooting

### Configuration:
- **HTTP port**: 8080 (configured in application.properties)
- **File upload limit**: 50MB (quarkus.http.limits.max-body-size)
- **TesseractOCR language**: Finnish ('fin')
- **OCR DPI**: 300 for high-quality text recognition

### Application Features:
- **Dual extraction**: PDFBox primary, TesseractOCR fallback
- **Finnish language support**: OCR optimized for Finnish text
- **Document type detection**: Automatic classification (INVOICE, RECEIPT, etc.)
- **Error handling**: Proper error responses for invalid PDFs

### Known Issues:
- **Configuration warning**: "quarkus.http.body.multipart.max-chunk-size" warning can be ignored
- **Font cache**: First PDF processing builds font cache (adds ~500ms delay)
- **Character encoding**: Some special characters may not render perfectly in simple PDFs

### Build Artifacts:
- **JAR file**: `target/pdf-extractor-1.0.0-SNAPSHOT.jar` (regular JAR)
- **Quarkus app**: `target/quarkus-app/` (runnable with `java -jar target/quarkus-app/quarkus-run.jar`)

## Performance Notes:
- **Initial dependency download**: First build takes 45-50 seconds
- **Subsequent builds**: 4-8 seconds for compile, 20-25 seconds for full package
- **Application startup**: 1-2 seconds in dev mode
- **PDF processing**: Varies by document complexity; first document slower due to font cache

## Critical Reminders:
- **NEVER CANCEL builds or tests** - Always wait for completion
- **ALWAYS install TesseractOCR** before running application
- **ALWAYS test end-to-end scenarios** after changes
- **ALWAYS format code** before committing changes
- **Set appropriate timeouts** (30+ seconds for builds, 60+ seconds for full package)