# PDF Text Extractor - Build and Test Makefile

.PHONY: help clean compile test package run docker-jvm docker-native test-multilang

# Default target
help:
	@echo "Available targets:"
	@echo "  clean          - Clean build artifacts"
	@echo "  compile        - Compile the application"
	@echo "  test           - Run tests"
	@echo "  package        - Build application package"
	@echo "  run            - Run in development mode"
	@echo "  docker-jvm     - Build JVM Docker image"
	@echo "  docker-native  - Build Native Docker image"
	@echo "  test-multilang - Create test PDFs and test multi-language support"

# Build targets
clean:
	@echo "Cleaning build artifacts..."
	mvn clean

compile:
	@echo "Compiling application..."
	mvn clean compile

test:
	@echo "Running tests..."
	mvn test

package:
	@echo "Building application package..."
	mvn clean package -DskipTests

# Run targets
run:
	@echo "Starting application in development mode..."
	mvn quarkus:dev

# Docker targets
docker-jvm: package
	@echo "Building JVM Docker image..."
	docker build -f Dockerfile.jvm -t pdf-extractor-jvm:latest .

docker-native:
	@echo "Building Native Docker image..."
	docker build -f Dockerfile.native -t pdf-extractor-native:latest .

# Test multi-language support
test-multilang:
	@echo "Creating test PDFs for multi-language testing..."
	@mkdir -p test-docs
	
	# Create Finnish test PDF
	@cat > test-docs/test_finnish.ps << 'EOF'
%!PS-Adobe-3.0
/Helvetica findfont 12 scalefont setfont
72 720 moveto
(Lasku numero: 12345) show
72 700 moveto
(Eräpäivä: 31.12.2024) show
72 680 moveto
(Yhteensä: 100,00 €) show
72 660 moveto
(Maksu suoritetaan tilille FI1234567890123456) show
showpage
EOF
	@which ps2pdf > /dev/null && ps2pdf test-docs/test_finnish.ps test-docs/test_finnish.pdf || echo "ps2pdf not available, skipping PDF creation"
	
	# Create Swedish test PDF
	@cat > test-docs/test_swedish.ps << 'EOF'
%!PS-Adobe-3.0
/Helvetica findfont 12 scalefont setfont
72 720 moveto
(Faktura nummer: 12345) show
72 700 moveto
(Förfallodag: 31.12.2024) show
72 680 moveto
(Totalt: 100,00 kr) show
72 660 moveto
(Betalning görs till konto SE1234567890123456) show
showpage
EOF
	@which ps2pdf > /dev/null && ps2pdf test-docs/test_swedish.ps test-docs/test_swedish.pdf || echo "ps2pdf not available, skipping PDF creation"
	
	# Create English test PDF
	@cat > test-docs/test_english.ps << 'EOF'
%!PS-Adobe-3.0
/Helvetica findfont 12 scalefont setfont
72 720 moveto
(Invoice Number: 12345) show
72 700 moveto
(Due Date: December 31, 2024) show
72 680 moveto
(Total Amount: $100.00 USD) show
72 660 moveto
(Payment to be made to account US1234567890123456) show
showpage
EOF
	@which ps2pdf > /dev/null && ps2pdf test-docs/test_english.ps test-docs/test_english.pdf || echo "ps2pdf not available, skipping PDF creation"
	
	@echo "Test documents created in test-docs/ directory"
	@echo "To test with running application:"
	@echo "  curl -X POST -F 'file=@test-docs/test_finnish.pdf' http://localhost:8080/api/pdf/extract"
	@echo "  curl -X POST -F 'file=@test-docs/test_swedish.pdf' http://localhost:8080/api/pdf/extract"
	@echo "  curl -X POST -F 'file=@test-docs/test_english.pdf' http://localhost:8080/api/pdf/extract"

# Clean test documents
clean-test:
	@echo "Cleaning test documents..."
	@rm -rf test-docs/

# Full test cycle
test-full: clean compile test package test-multilang
	@echo "Full test cycle completed"

# Development workflow
dev: clean compile test
	@echo "Development build completed, starting application..."
	mvn quarkus:dev