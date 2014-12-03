package com.aconex;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.aconex.index.WordIndex;

/**
 * Test class to ensure actual number matching works correctly.
 */
public class NumberMatcherUnitTest {

    private WordIndex createSmallWordIndex() {
        //create the test index containing the smaller word set.
        final WordIndex wordIndex = new WordIndex();
        wordIndex.loadIndex(NumberMatcherUnitTest.class.getResourceAsStream("/com/aconex/index/sampleDict"));
        return wordIndex;
    }

    @Test
    public void testConstructionFailsWithoutWordIndex() {
        try {
            new NumberMatcher(null, "193.12-3922");
            Assert.fail("An exception should have been thrown at construction as there wasn't a WordIndex provided.");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid IllegalArgumentException message", "Word Index cannot be null", e.getMessage());
        }
    }

    @Test
    public void testConstructionFailsWithEmptyNumberAsString() {
        try {
            new NumberMatcher(new WordIndex(), "");
            Assert.fail("An exception should have been thrown at construction as number as string was empty.");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid IllegalArgumentException message", "NumberAsString cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testGetMatchingValueRemovesWhitespaceAndPunctuation() {
        final NumberMatcher numberMatcher = new NumberMatcher(new WordIndex(), "12-1234,123");
        Assert.assertEquals("Unexpected value for number being matched", Integer.valueOf(121234123), numberMatcher.getMatchingValue());
    }

    @Test
    public void testGetMatchingValueRemoves1800Prefix() {
        final NumberMatcher numberMatcher = new NumberMatcher(new WordIndex(), "1800123543");
        Assert.assertEquals("Unexpected value for number being matched", Integer.valueOf(123543), numberMatcher.getMatchingValue());
    }

    @Test
    public void testGetMatchingValueReturnsNullWhenLettersAreInTheValue() {
        final NumberMatcher numberMatcher = new NumberMatcher(new WordIndex(), "18A0123543");
        Assert.assertEquals("Unexpected value for number being matched", null, numberMatcher.getMatchingValue());
    }

    @Test
    public void testGetMatchesFindsFullMatches() {

        final NumberMatcher numberMatcher = new NumberMatcher(createSmallWordIndex(), "1800-437-245");

        final Set<String> matches = numberMatcher.findMatches();
        Assert.assertNotNull("Matches shouldn't be null", matches);
        Assert.assertEquals("There should be 1 match for the number provided", 1, matches.size());
        Assert.assertTrue("Invalid matching number", matches.contains("1-800-GERBIL"));
    }

    @Test
    public void testGetMatchesFindsMultiWordMatches() {
        final NumberMatcher numberMatcher = new NumberMatcher(createSmallWordIndex(), "1800-843-728");

        final Set<String> matches = numberMatcher.findMatches();
        Assert.assertNotNull("Matches shouldn't be null", matches);
        Assert.assertEquals("There should be 1 match for the number provided", 1, matches.size());
        Assert.assertTrue("Invalid matching number", matches.contains("1-800-THE-RAT"));
    }

    @Test
    public void testGetMatchesFindsMultiWordMatchesWithRemainingDigit() {
        final NumberMatcher numberMatcher = new NumberMatcher(createSmallWordIndex(), "1800-462882");

        final Set<String> matches = numberMatcher.findMatches();
        Assert.assertNotNull("Matches shouldn't be null", matches);
        Assert.assertEquals("There should be 1 match for the number provided", 1, matches.size());
        Assert.assertTrue("Invalid matching number", matches.contains("1-800-GOAT-8-A"));
    }
}
