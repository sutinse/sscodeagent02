package fi.sutinse.pdfextractor.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PdfExtractionResourceTest {

  @Test
  public void testHealthEndpoint() {
    given()
        .when()
        .get("/api/pdf/health")
        .then()
        .statusCode(200)
        .body("service", is("PDF Extractor Service"))
        .body("status", is("UP"));
  }

  @Test
  public void testEmptyFileUpload() {
    given()
        .when()
        .post("/api/pdf/extract")
        .then()
        .statusCode(415); // Unsupported Media Type, no JSON body expected
  }

  @Test
  public void testInvalidFileType() {
    byte[] invalidContent = "Not a PDF".getBytes();

    given()
        .multiPart("file", "test.txt", invalidContent, "text/plain")
        .when()
        .post("/api/pdf/extract")
        .then()
        .statusCode(400)
        .body("success", is(false))
        .body("errorMessage", notNullValue());
  }

  @Test
  public void testInvalidPdfContent() {
    byte[] invalidPdfContent = "Invalid PDF content".getBytes();

    given()
        .multiPart("file", "invalid.pdf", invalidPdfContent, "application/pdf")
        .when()
        .post("/api/pdf/extract")
        .then()
        .statusCode(500) // Internal Server Error for invalid PDF format
        .body("success", is(false))
        .body("errorMessage", notNullValue());
  }
}
