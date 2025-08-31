package fi.sutinse.pdfextractor.resource;

import fi.sutinse.pdfextractor.dto.PdfExtractionResponse;
import fi.sutinse.pdfextractor.service.PdfExtractionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** REST resource for PDF text extraction */
@Path("/api/pdf")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PdfExtractionResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(PdfExtractionResource.class);

  @Inject PdfExtractionService pdfExtractionService;

  /**
   * Extracts text from uploaded PDF file
   *
   * @param file Uploaded PDF file
   * @return Extraction response with text and metadata
   */
  @POST
  @Path("/extract")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response extractText(@RestForm("file") FileUpload file) {
    if (file == null) {
      LOGGER.warn("No file received in request");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity(PdfExtractionResponse.failure("No file data provided"))
          .build();
    }

    String filename = file.fileName();
    LOGGER.info("Received PDF extraction request for file: {}", filename);

    try {
      // Validate file type
      if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
        LOGGER.warn("Invalid file type received: {}", filename);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(PdfExtractionResponse.failure("Only PDF files are supported"))
            .build();
      }

      // Read file data
      byte[] fileData = Files.readAllBytes(file.uploadedFile());

      if (fileData.length == 0) {
        LOGGER.warn("Empty file received");
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(PdfExtractionResponse.failure("Empty file received"))
            .build();
      }

      // Extract text
      PdfExtractionResponse response = pdfExtractionService.extractText(fileData, filename);

      if (response.success()) {
        LOGGER.info(
            "PDF extraction successful for file: {}, method: {}, type: {}",
            filename,
            response.methodUsed(),
            response.documentType());
        return Response.ok(response).build();
      } else {
        LOGGER.error(
            "PDF extraction failed for file: {}, error: {}", filename, response.errorMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
      }

    } catch (IOException e) {
      LOGGER.error("Error reading uploaded file: {}", filename, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(PdfExtractionResponse.failure("Error reading file: " + e.getMessage()))
          .build();
    } catch (Exception e) {
      LOGGER.error("Unexpected error during PDF extraction", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(PdfExtractionResponse.failure("Internal server error: " + e.getMessage()))
          .build();
    }
  }

  /** Health check endpoint */
  @GET
  @Path("/health")
  public Response health() {
    return Response.ok().entity(new HealthResponse("PDF Extractor Service", "UP")).build();
  }

  /** Record for health response */
  public record HealthResponse(String service, String status) {}
}
