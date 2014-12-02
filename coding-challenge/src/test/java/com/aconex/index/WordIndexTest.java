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
    public void testLoadIndexAddsAnotherItemToTheCount() {
        final WordIndex wordIndex = new WordIndex();
        wordIndex.loadIndex("goat");

        Assert.assertTrue("Adding the text \"goat\" should add something to the indexed count.", wordIndex.getIndexedCount() == 1);
    }

    @Test
    public void testLoadIndexIgnoresPunctuationAndWhitespace() {
        final WordIndex wordIndex = new WordIndex();

        wordIndex.loadIndex("goats .eat,hay");

        Assert.assertTrue("Adding the text \"goats are number.one\" should add something to the indexed count.", wordIndex.getIndexedCount() == 1);
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
