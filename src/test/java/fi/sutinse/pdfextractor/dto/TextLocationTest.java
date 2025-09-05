package fi.sutinse.pdfextractor.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TextLocationTest {

  @Test
  void testTextLocation() {
    TextLocation location = TextLocation.of(10, 20, 100, 25, 1);

    assertEquals(10, location.x());
    assertEquals(20, location.y());
    assertEquals(100, location.width());
    assertEquals(25, location.height());
    assertEquals(1, location.pageNumber());
  }

  @Test
  void testTextLocationConstructor() {
    TextLocation location = new TextLocation(50, 60, 200, 30, 2);

    assertEquals(50, location.x());
    assertEquals(60, location.y());
    assertEquals(200, location.width());
    assertEquals(30, location.height());
    assertEquals(2, location.pageNumber());
  }
}
