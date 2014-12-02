package com.aconex.index;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class WordIndexTest {
    @Test
    public void testLoadingTheIndexFailsWhenTheFileIsMissing() throws IOException {
        final WordIndex wordIndex = new WordIndex();

        try {
            wordIndex.loadIndex(Paths.get("thisFileIsMissing"));
            Assert.fail("We should have thrown an IOException if the file is missing.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("File missing"));
        }
    }

    @Test
    public void testLoadingTheIndexFailsWhenTheFileIsNotText() {
        final WordIndex wordIndex = new WordIndex();
        try {
            wordIndex.loadIndex(Paths.get(WordIndexTest.class.getResource("invalidFile.jpg").getPath()));
            Assert.fail("An IOException should have been thrown as the file isn't readable as text.");
        } catch (IOException e) {
            //this is an expected error so we don't need to do anything here
        }
    }

    @Test
    public void testInstanceCreationHasEmptyIndex() {
        final WordIndex wordIndex = new WordIndex();
        Assert.assertEquals("Expected the initialised word index to have no values", 0, wordIndex.getIndexedCount());
    }

    @Test
    public void testLoadIndexIgnoresPunctuationAndWhitespaceAndAddsItemsToTheIndex() {
        final WordIndex wordIndex = new WordIndex();

        final String testEntry = "go .eat,ha";
        wordIndex.loadIndex(testEntry);

        Assert.assertEquals(String.format("Adding the text \"%s\" should add something to the indexed count.", testEntry), 1, wordIndex.getIndexedCount());
    }

    @Test
    public void testLoadIndexWithAValueThatWillOverflowAnIntWillNotAddTheValue() {
        final WordIndex wordIndex = new WordIndex();

        final String testEntry = "This is a really long word that is going to be ignored by the indexer as the conversion will be a large number that won't be useful for mapping anyway";
        wordIndex.loadIndex(testEntry);
        Assert.assertEquals(String.format("Shouldn't have been able to add \"%s\" to the index as it was too long.", testEntry), 0, wordIndex.getIndexedCount());
    }

    @Test
    public void testLoadIndexUsingPathAddsMultipleItemsToTheCount() throws IOException {
        final WordIndex wordIndex = new WordIndex();
        wordIndex.loadIndex(Paths.get(WordIndexTest.class.getResource("sampleDict").getPath()));

        Assert.assertEquals("There should be 3 entries in the index from the test file", 3, wordIndex.getIndexedCount());
    }

    @Test
    public void testFindValuesForNumber() {
        final WordIndex wordIndex = new WordIndex();
        //load using lowercase to ensure that case insensitivity is honored.
        wordIndex.loadIndex("cat");
        final Set<String> stringValues = wordIndex.search(228);
        Assert.assertTrue("Adding the text \"cat\" should add something to the indexed count.", wordIndex.getIndexedCount() == 1);
        Assert.assertTrue("Should have been able to locate the loaded value \"CAT\" using the number equivalent.", stringValues != null && stringValues.contains("CAT"));
    }
}
