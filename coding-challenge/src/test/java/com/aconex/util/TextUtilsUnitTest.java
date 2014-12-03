package com.aconex.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case to handle testing functions for parsing text.
 */
public class TextUtilsUnitTest {
    @Test
    public void testThatPunctuationAndWhitespaceAreRemovedWhenStrippingRedundantCharacters() {
        final String testValue = "this Is.Some;,Text";
        final String expectedResult = "thisIsSomeText";

        Assert.assertEquals("Unexpected result for removing punctuation and whitespace.", expectedResult, TextUtils.stripRedundantCharacters(testValue));
    }

    @Test
    public void testNullIsReturnedForEmptyStringWhenStrippingRedundantCharacters() {
        Assert.assertEquals("Unexpected result for removing punctuation and whitespace.", null, TextUtils.stripRedundantCharacters(""));
    }

    @Test
    public void testNullIsReturnedForStringContainingAllPunctuationAndWhitespaceWhenStrippingRedundantCharacters() {
        Assert.assertEquals("Unexpected result for removing punctuation and whitespace.", null, TextUtils.stripRedundantCharacters(" ;, '' ..."));
    }

    @Test
    public void testNullIsReturnedForNullStringWhenStrippingRedundantCharacters() {
        Assert.assertEquals("Unexpected result for removing punctuation and whitespace.", null, TextUtils.stripRedundantCharacters(null));
    }

    @Test
    public void testJoinAs1800NumberJoinsWithDashesAndAddsPrefix() {
        Assert.assertEquals("Unexpected value from joining as 1800 number", "1-800-GOAT-COW", TextUtils.joinAs1800Number("GOAT", "COW"));
    }
}
