package de.codecentric.mule.assertobjectequals;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PathPatternParserTest {
    private PathPatternParser parser;

    @Before
    public void before() {
        parser = new PathPatternParser();
    }

    @After
    public void after() {
        parser = null;
    }

    @Test
    public void emtpyStringGivesEmptyPattern() {
        String pathAsString = "";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(0, pp.size());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void questionmarkGivesWildcardOne() {
        String pathAsString = "?";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.WILDCARD_ONE, pp.getEntry(0).getType());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void starGivesWildcardAny() {
        String pathAsString = "*";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.WILDCARD_ANY, pp.getEntry(0).getType());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void hashGivesListAny() {
        String pathAsString = "[#]";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.LIST, pp.getEntry(0).getType());
        assertNull(pp.getEntry(0).getListIndex());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void numberGivesListSpecific() {
        String pathAsString = "[42]";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.LIST, pp.getEntry(0).getType());
        assertEquals(42, (int) pp.getEntry(0).getListIndex());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void quotedEmptyTextGivesMap() {
        String pathAsString = "['']";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
        assertEquals("", pp.getEntry(0).getKeyPattern().pattern());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void quotedTextGivesMap() {
        String pathAsString = "['foo']";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
        assertEquals("foo", pp.getEntry(0).getKeyPattern().pattern());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void quotedTextWithEscapedCharsGivesMap() {
        String pathAsString = "['fo''o']";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
        assertEquals("fo'o", pp.getEntry(0).getKeyPattern().pattern());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void quotedTextWithEscapedCharsAtEndGivesMap() {
        String pathAsString = "['foo''']";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
        assertEquals("foo'", pp.getEntry(0).getKeyPattern().pattern());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void quotedTextWithEscapedCharsAtStartGivesMap() {
        String pathAsString = "['''bar']";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
        assertEquals("'bar", pp.getEntry(0).getKeyPattern().pattern());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void quotedTextWithDoubleEscapeCharsGivesMap() {
        String pathAsString = "['fo''''so']";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.MAP, pp.getEntry(0).getType());
        assertEquals("fo''so", pp.getEntry(0).getKeyPattern().pattern());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }

    @Test
    public void spaceButNoOptions() {
        String pathAsString = "? ";
        PathPattern pp = parser.parse(pathAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.WILDCARD_ONE, pp.getEntry(0).getType());
        assertEquals(pathAsString.trim(), pp.toString());
        assertTrue(pp.getOptions().isEmpty());
    }
    
    @Test
    public void withOptions() {
        String pathAsString = "?";
        String optionsAsString = "contains_only_on_maps check_map_order ignore"; 
        PathPattern pp = parser.parse(pathAsString + " " + optionsAsString);
        assertEquals(1, pp.size());
        assertEquals(PatternEntry.PatternEntryType.WILDCARD_ONE, pp.getEntry(0).getType());
        assertEquals(pathAsString, pp.toString());
        assertTrue(pp.getOptions().contains(PathOption.IGNORE));
        assertTrue(pp.getOptions().contains(PathOption.CHECK_MAP_ORDER));
        assertTrue(pp.getOptions().contains(PathOption.CONTAINS_ONLY_ON_MAPS));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void withBadOptions() {
        String pathAsString = "?";
        String optionsAsString = "foo"; 
        parser.parse(pathAsString + " " + optionsAsString);
    }
}
