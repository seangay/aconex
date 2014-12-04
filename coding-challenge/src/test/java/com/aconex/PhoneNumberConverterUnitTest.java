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
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.mockito.Mockito;

import com.aconex.index.WordIndex;

/**
 * Tests functionality for the main command line class.
 */
public class PhoneNumberConverterUnitTest {
    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();
    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties(PhoneNumberConverter.SYS_PROP_DICTIONARY_OVERRIDE);

    @Test
    public void testProcessFileWithAnInvalidFileLocation() {
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter(mock(WordIndex.class));
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
    public void testProcessFileWithAValidFileContainingNumbers() {
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter(mock(WordIndex.class));
        phoneNumberConverter.processFile(PhoneNumberConverterUnitTest.class.getResource("testNumberFile").getPath());

        //get the output and make sure that there is the text in there for the line in the file we expect.
        final String logValue = log.getLog();
        Assert.assertEquals("Unexpected output for phone number processing line.", logValue.split("\n")[1], "Processing phone number 1800-233-333.");
    }

    @Test
    public void testInitialisationUsesOverrideForDictionaryWhenSet() throws URISyntaxException, IOException {
        final String testSystemProperty = PhoneNumberConverterUnitTest.class.getResource("/com/aconex/index/sampleDict").getPath();
        System.setProperty(PhoneNumberConverter.SYS_PROP_DICTIONARY_OVERRIDE, testSystemProperty);
        final WordIndex mockWordIndex = mock(WordIndex.class);

        //inject the mock and call the method being tested
        new PhoneNumberConverter(mockWordIndex);

        Mockito.verify(mockWordIndex).loadIndex(isA(FileInputStream.class));
    }

    @Test
    public void testInitialisationUsesDefaultWhenNoOverrideIsSet() throws IOException {
        //make sure that there isn't a system property here first.
        final String dictionarySystemPropertyValue = System.getProperty(PhoneNumberConverter.SYS_PROP_DICTIONARY_OVERRIDE);
        Assert.assertNull(String.format("System property for %s should have been null. It was set to %s", PhoneNumberConverter.SYS_PROP_DICTIONARY_OVERRIDE, dictionarySystemPropertyValue), dictionarySystemPropertyValue);
        final WordIndex mockWordIndex = mock(WordIndex.class);

        //inject the mock and call the method being tested
        new PhoneNumberConverter(mockWordIndex);
        Mockito.verify(mockWordIndex).loadIndex(isA(BufferedInputStream.class));
    }

    @Test
    public void testProcessNumberWritesOutValuesForANumberThatFindsMatches() {
        //load a smaller dictionary into the index through overriding the system property
        final String testSystemProperty = PhoneNumberConverterUnitTest.class.getResource("/com/aconex/index/sampleDict").getPath();
        System.setProperty(PhoneNumberConverter.SYS_PROP_DICTIONARY_OVERRIDE, testSystemProperty);
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
        phoneNumberConverter.processNumber("228");

        //get the output and make sure that there is the text in there for the line in the file we expect.
        final String logValue = log.getLog();
        Assert.assertNotNull("There should be output written for a valid number that has matches.", logValue);
        final String[] outputLines = logValue.split("\n");
        Assert.assertEquals("Should be 4 lines of output for the test number.", outputLines.length, 4);

        //the first line should state what is being processed.
        Assert.assertEquals("Unexpected output for phone number processing line.", outputLines[0], "Processing phone number 228.");
        //as we are processing a set of values I don't rely here on the lines having specific output. Just the log contains the numbers we expect.
        Assert.assertTrue("Log should have contained: 1-800-BAT", logValue.contains("1-800-BAT"));
        Assert.assertTrue("Log should have contained: 1-800-CAT", logValue.contains("1-800-CAT"));
    }
}
