package fi.sutinse.pdfextractor.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class StructuredTextTest {

  @Test
  void testFromText() {
    String text = "This is a test document.";
    StructuredText structuredText = StructuredText.fromText(text);
    
    assertEquals(text, structuredText.content());
    assertEquals(1, structuredText.elements().size());
    assertEquals(text, structuredText.elements().get(0).text());
    assertNull(structuredText.elements().get(0).locations());
    assertFalse(structuredText.hasLocationData());
  }

  @Test
  void testFromElements() {
    String content = "Hello world";
    TextLocation location = TextLocation.of(10, 20, 50, 15, 1);
    TextElement element = TextElement.withLocations("Hello", List.of(location));
    List<TextElement> elements = List.of(element);
    
    StructuredText structuredText = StructuredText.fromElements(content, elements);
    
    assertEquals(content, structuredText.content());
    assertEquals(1, structuredText.elements().size());
    assertEquals("Hello", structuredText.elements().get(0).text());
    assertNotNull(structuredText.elements().get(0).locations());
    assertEquals(1, structuredText.elements().get(0).locations().size());
    assertTrue(structuredText.hasLocationData());
  }
}