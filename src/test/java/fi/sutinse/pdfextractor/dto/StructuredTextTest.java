package fi.sutinse.pdfextractor.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
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
    assertTrue(structuredText.structuredData().isEmpty());
  }

  @Test
  void testFromTextWithStructuredData() {
    String text = "This is a test document.";
    Map<String, Object> structuredData = Map.of("test_field", "test_value");
    StructuredText structuredText = StructuredText.fromText(text, structuredData);

    assertEquals(text, structuredText.content());
    assertEquals(1, structuredText.elements().size());
    assertEquals(text, structuredText.elements().get(0).text());
    assertNull(structuredText.elements().get(0).locations());
    assertFalse(structuredText.hasLocationData());
    assertEquals(1, structuredText.structuredData().size());
    assertEquals("test_value", structuredText.structuredData().get("test_field"));
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
    assertTrue(structuredText.structuredData().isEmpty());
  }

  @Test
  void testFromElementsWithStructuredData() {
    String content = "Hello world";
    TextLocation location = TextLocation.of(10, 20, 50, 15, 1);
    TextElement element = TextElement.withLocations("Hello", List.of(location));
    List<TextElement> elements = List.of(element);
    Map<String, Object> structuredData = Map.of("greeting", "Hello", "target", "world");

    StructuredText structuredText = StructuredText.fromElements(content, elements, structuredData);

    assertEquals(content, structuredText.content());
    assertEquals(1, structuredText.elements().size());
    assertEquals("Hello", structuredText.elements().get(0).text());
    assertNotNull(structuredText.elements().get(0).locations());
    assertEquals(1, structuredText.elements().get(0).locations().size());
    assertTrue(structuredText.hasLocationData());
    assertEquals(2, structuredText.structuredData().size());
    assertEquals("Hello", structuredText.structuredData().get("greeting"));
    assertEquals("world", structuredText.structuredData().get("target"));
  }
}
