package com.aconex;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Set;

import com.aconex.index.WordIndex;
import com.aconex.util.TextUtils;

/**
 * Converts numbers to strings based on phone number --> letter conversion equivalents.
 */
public class PhoneNumberConverter {
    public static final String SYS_PROP_DICTIONARY_OVERRIDE = "dictionary.file";

    private WordIndex wordIndex;

    public PhoneNumberConverter() {
        this(new WordIndex());
    }

    public PhoneNumberConverter(final WordIndex wordIndex) {
        this.wordIndex = (wordIndex != null) ? wordIndex : new WordIndex();
        initialise();
    }

    private void initialise() {
        String dictionaryFileLocation = System.getProperty(SYS_PROP_DICTIONARY_OVERRIDE);
        InputStream inputStream = PhoneNumberConverter.class.getResourceAsStream("dictionary");
        if (!TextUtils.isEmpty(dictionaryFileLocation)) {
            try {
                inputStream = new FileInputStream(dictionaryFileLocation);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Couldn't locate dictionary file to be loaded.", e);
            }
        }
        wordIndex.loadIndex(inputStream);
    }

    public void processFile(final String filePath) {
        //process each file in turn and convert each one.
        System.out.println(String.format("Processing file: %s", filePath));
        try {
            try (Scanner scanner = new Scanner(new FileInputStream(filePath))) {
                while (scanner.hasNext()) {
                    processNumber(scanner.nextLine());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Skipping processing \"%s\" as the file couldn't be located.", filePath));
        }
    }

    public void processNumber(final String number) {
        System.out.println(String.format("Processing phone number %s.", number));
        final NumberMatcher numberMatcher = new NumberMatcher(wordIndex, number);
        final Set<String> matches = numberMatcher.findMatches();

        if (!matches.isEmpty()) {
            System.out.println(String.format("Found %d options: ", matches.size()));
            for (String match : matches) {
                System.out.println(match);
            }
        } else {
            System.out.println("No options found.");
        }
    }
}
