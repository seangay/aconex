package com.aconex;

import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

import com.aconex.index.WordIndex;

/**
 * Converts numbers to strings based on phone number --> letter conversion equivalents.
 */
public class PhoneNumberConverter {
    private static final String EXIT_COMMAND = "exit";
    public static final String SYS_PROP_DICTIONARY_OVERRIDE = "dictionary.file";

    private WordIndex wordIndex = new WordIndex();

    public static void main(String[] args) {
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
        phoneNumberConverter.initialise();

        if (args == null || args.length == 0) {
            Console console = System.console();

            if (console == null) {
                System.err.println("No console is available to accept input. Please supply a file containing numbers to convert to words.");
                System.exit(1);
            }
            boolean keepRunning = true;
            while (keepRunning) {
                String number = console.readLine(String.format("Enter a number find matching words, or '%s' to quit: ", EXIT_COMMAND));
                if (EXIT_COMMAND.equals(number)) {
                    keepRunning = false;
                } else {
                    phoneNumberConverter.processNumber(number);
                }
            }
        } else {
            System.out.println("Files to process: " + Arrays.toString(args));
            for (String arg : args) {
                phoneNumberConverter.processFile(arg);
            }
        }
        System.exit(0);
    }

    void initialise() {
        String dictionaryFileLocation = System.getProperty(SYS_PROP_DICTIONARY_OVERRIDE);
        InputStream inputStream = PhoneNumberConverter.class.getResourceAsStream("dictionary");
        if (dictionaryFileLocation != null && !dictionaryFileLocation.isEmpty()) {
            try {
                inputStream = new FileInputStream(dictionaryFileLocation);
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Couldn't locate dictionary file to be loaded.", e);
            }
        }
        wordIndex.loadIndex(inputStream);
    }

    void processFile(final String filePath) {
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

    void processNumber(final String number) {
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

    void setWordIndex(final WordIndex wordIndex) {
        this.wordIndex = wordIndex;
    }
}
