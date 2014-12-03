package com.aconex;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.mockito.Mockito;

import com.aconex.index.WordIndex;

/**
 * Tests functionality for the main command line class.
 */
public class PhoneNumberConverterUnitTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();

    @Test
    public void testSystemExitsWhenNoArgsAreProvidedAsThereIsNoConsoleForTesting() {
        exit.expectSystemExitWithStatus(1);
        PhoneNumberConverter.main(null);
    }

    @Test
    public void testSystemExitValueWhenArgsAreProvided() {
        exit.expectSystemExitWithStatus(0);
        PhoneNumberConverter.main(new String[]{ "argumentZero" });
    }

    @Test
    public void testArgsThatWillNotResolveToAFile() {
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
        final String testFilePath = "invalidFileLocation";
        phoneNumberConverter.processFile(testFilePath);

        //get the content of what was written and split on new lines so tests can be done.
        final String logValue = log.getLog();
        Assert.assertNotNull("There should be output written for an invalid file argument.", logValue);
        final String[] outputLines = logValue.split("\n");
        Assert.assertEquals("Should be 2 lines of output for a file that can't be found.", outputLines.length, 2);

        //the first line should state what is being processed.
        Assert.assertEquals("Unexpected first line of output.", String.format("Processing file: %s", testFilePath), outputLines[0]);
        Assert.assertEquals("Unexpected second line of output.", String.format("Skipping processing \"%s\" as the file couldn't be located.", testFilePath), outputLines[1]);
    }

    @Test
    public void testArgsThatFindFilesWillPrintNumbersBeingProcessed() {
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
        phoneNumberConverter.processFile(PhoneNumberConverterUnitTest.class.getResource("testNumberFile").getPath());

        //get the output and make sure that there is the text in there for the line in the file we expect.
        final String logValue = log.getLog();
        Assert.assertEquals(logValue.split("\n")[1], "Processing phone number 1800-233-333.");
    }

    @Test
    public void testInitialisationUsesOverrideForDictionaryWhenSet() throws URISyntaxException, IOException {
        final String testSystemProperty = PhoneNumberConverterUnitTest.class.getResource("/com/aconex/index/sampleDict").getPath();
        System.setProperty(PhoneNumberConverter.SYS_PROP_DICTIONARY_OVERRIDE, testSystemProperty);
        final WordIndex mockWordIndex = mock(WordIndex.class);

        //inject the mock and call the method being tested
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
        phoneNumberConverter.setWordIndex(mockWordIndex);
        phoneNumberConverter.initialise();

        Mockito.verify(mockWordIndex).loadIndex(isA(FileInputStream.class));
    }

    @Test
    public void testInitialisationUsesDefaultWhenNoOverrideIsSet() throws IOException {
        //make sure that there isn't a system property here first.
        final String dictionarySystemPropertyValue = System.getProperty(PhoneNumberConverter.SYS_PROP_DICTIONARY_OVERRIDE);
        Assert.assertNull(String.format("System property for %s should have been null. It was set to %s", PhoneNumberConverter.SYS_PROP_DICTIONARY_OVERRIDE, dictionarySystemPropertyValue), dictionarySystemPropertyValue);
        final WordIndex mockWordIndex = mock(WordIndex.class);

        //inject the mock and call the method being tested
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
        phoneNumberConverter.setWordIndex(mockWordIndex);
        phoneNumberConverter.initialise();

        Mockito.verify(mockWordIndex).loadIndex(isA(BufferedInputStream.class));
    }
}
