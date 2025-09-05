package fi.sutinse.pdfextractor.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class TextElementTest {

  @Test
  void testTextOnly() {
    String text = "Sample text";
    TextElement element = TextElement.textOnly(text);

    assertEquals(text, element.text());
    assertNull(element.locations());
  }

  @Test
  void testWithLocations() {
    String text = "Word";
    TextLocation location1 = TextLocation.of(10, 20, 40, 15, 1);
    TextLocation location2 = TextLocation.of(55, 20, 50, 15, 1);
    List<TextLocation> locations = List.of(location1, location2);

    TextElement element = TextElement.withLocations(text, locations);

    assertEquals(text, element.text());
    assertNotNull(element.locations());
    assertEquals(2, element.locations().size());
    assertEquals(location1, element.locations().get(0));
    assertEquals(location2, element.locations().get(1));
  }
}
