package com.aconex.index;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;


/**
 * Controls mapping of numbers to Strings that could be derived using a standard phone handset.
 */
public class WordIndex {
    private final static Logger LOGGER = Logger.getLogger(WordIndex.class.getName());
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
     * Loads the entries in the file located through the provided path into the index.
     * <p/>
     * Any entries that contain characters which can't be written using a standard phone keypad will not be added to the
     * index.
     *
     * @param path the path to a file that is to be processed. Each line of the file should contain a new word to add
     * @throws IOException              if the file can't be read
     * @throws IllegalArgumentException if the file can't be located
     */
    public void loadIndex(@NotNull final Path path) throws IOException {
        final boolean dictionaryExists = Files.exists(path);
        if (!dictionaryExists) {
            throw new IllegalArgumentException(String.format("File missing: %s cannot be found.", path));
        }
        final List<String> entryLines = Files.readAllLines(path, Charset.defaultCharset());
        for (String entryLine : entryLines) {
            loadIndex(entryLine);
        }
    }

    /**
     * Loads an individual entry into the index. If the new entry contains ANY characters that can't be written using a
     * standard phone keypad it will not be added to the index.
     *
     * @param newEntry the new entry to add.
     */
    public void loadIndex(@NotNull final String newEntry) {
        final StringBuilder numberBuilder = new StringBuilder();

        final String upperCaseEntry = newEntry.replaceAll("\\W", "").toUpperCase();
        for (char newEntryChar : upperCaseEntry.toCharArray()) {
            //determine what the corresponding value should be for the current char
            int numberValue = getNumberEncoding(newEntryChar);

            if (numberValue < 0) {
                //need to return here due to the word containing invalid items that can't be mapped to a number.
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE, "Not adding value {0} to file as it contained {1} which can't be mapped to a number.", new Object[]{ newEntry, newEntryChar });
                }
                return;
            } else {
                numberBuilder.append(numberValue);
            }
        }

        int entryNumberValue = Integer.parseInt(numberBuilder.toString());
        Set<String> currentValuesForNumber = wordMap.get(entryNumberValue);
        if (currentValuesForNumber == null) {
            currentValuesForNumber = new HashSet<>();
            wordMap.put(entryNumberValue, currentValuesForNumber);
        }
        currentValuesForNumber.add(upperCaseEntry);
    }

    /**
     * Searches the index for any values that match the number provided. If there are no matching entries null will be
     * returned.
     */
    @Nullable
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
