package fi.sutinse.pdfextractor.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

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
}
