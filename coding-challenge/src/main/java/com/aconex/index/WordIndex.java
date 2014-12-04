package com.aconex.index;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.aconex.util.TextUtils;

/**
 * Controls mapping of numbers to Strings that could be derived using a standard phone handset.
 */
public class WordIndex {
    private static final Logger LOGGER = Logger.getLogger(WordIndex.class.getName());
    private Map<Integer, Set<String>> wordMap = new HashMap<>();

    /**
     * Gets the total number of String values that are held by the index at the current time.
     */
    public int getIndexedCount() {
        int count = 0;
        for (Set<String> values : wordMap.values()) {
            count += values.size();
        }
        return count;
    }

    /**
     * Reads the entire content of the stream processing each line as it finds it.
     *
     * @param inputStream the inputStream containing the content to be loaded.
     */
    public void loadIndex(final InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Stream cannot be null");
        }

        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNext()) {
                loadIndex(scanner.nextLine());
            }
        }
    }

    /**
     * Loads an individual entry into the index. If the new entry contains ANY characters that can't be written using a
     * standard phone keypad it will not be added to the index.
     *
     * @param newEntry the new entry to add.
     */
    public void loadIndex(final String newEntry) {
        final StringBuilder builder = new StringBuilder();

        //process the input and remove redundant characters then convert to uppercase to reflect desired output.
        final String processedEntry = TextUtils.stripRedundantCharacters(newEntry).toUpperCase();
        for (char currentChar : processedEntry.toCharArray()) {
            //determine what the corresponding value should be for the current char
            int encodedChar = getNumberEncoding(currentChar);
            if (isValidEncoding(encodedChar)) {
                builder.append(encodedChar);
            } else {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Not adding value {0} to file as it contained {1} which can't be mapped to a number.", new Object[]{ newEntry, currentChar });
                }
                return;
            }
        }
        addToIndex(processedEntry, builder.toString());
    }

    private boolean isValidEncoding(final int encodedChar) {
        return encodedChar > 0;
    }

    private void addToIndex(final String processedEntry, final String newEntryConvertedToNumbers) {
        try {
            int entryNumberValue = Integer.parseInt(newEntryConvertedToNumbers);
            getEntriesFromMap(entryNumberValue).add(processedEntry);
        } catch (NumberFormatException e) {
            //the dictionary may have very large words in it that don't fit into a standard int. Just drop them if this happens. Chances are they aren't going to be useful for mapping anyway.
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Not adding value {0} to file as it was too long for a standard number.", new Object[]{ newEntryConvertedToNumbers });
            }
        }
    }

    private Set<String> getEntriesFromMap(final int key) {
        Set<String> entries = wordMap.get(key);
        if (entries == null) {
            entries = new HashSet<>();
            wordMap.put(key, entries);
        }
        return entries;
    }

    /**
     * Searches the index for any values that match the number provided. If there are no matching entries null will be
     * returned.
     */
    public Set<String> search(int number) {
        return wordMap.get(number);
    }

    private int getNumberEncoding(char c) {
        switch (c) {
            case 'A':
            case 'B':
            case 'C':
                return 2;
            case 'D':
            case 'E':
            case 'F':
                return 3;
            case 'G':
            case 'H':
            case 'I':
                return 4;
            case 'J':
            case 'K':
            case 'L':
                return 5;
            case 'M':
            case 'N':
            case 'O':
                return 6;
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
                return 7;
            case 'T':
            case 'U':
            case 'V':
                return 8;
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                return 9;
            default:
                return -1;
        }
    }
}
