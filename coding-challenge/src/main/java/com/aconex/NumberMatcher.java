package com.aconex;

import java.util.HashSet;
import java.util.Set;

import com.aconex.index.WordIndex;
import com.aconex.util.TextUtils;

/**
 * Class to do the heavy work when it comes to the logic for matching numbers to words.
 */
public class NumberMatcher {
    private Integer matchingValue;
    private WordIndex wordIndex;

    /**
     * Constructor.
     *
     * @param wordIndex      the index containing values that can be matched. Must not be null.
     * @param numberAsString the string representation of the value to get matching words for the number. Must not be empty.
     */
    public NumberMatcher(final WordIndex wordIndex, final String numberAsString) {
        checkArguments(wordIndex, numberAsString);
        this.wordIndex = wordIndex;
        this.matchingValue = determineMatchingValue(numberAsString);
    }

    private Integer determineMatchingValue(final String numberAsString) {
        String strippedValue = TextUtils.stripRedundantCharacters(numberAsString);

        Integer matchingValue = null;
        if (strippedValue != null) {
            strippedValue = strippedValue.replaceFirst("1800", "");
            try {
                matchingValue = Integer.valueOf(strippedValue);
            } catch (NumberFormatException e) {
                matchingValue = null;
            }
        }
        return matchingValue;
    }

    private void checkArguments(final WordIndex wordIndex, final String numberAsString) {
        if (wordIndex == null) {
            throw new IllegalArgumentException("Word Index cannot be null");
        }

        if (TextUtils.isEmpty(numberAsString)) {
            throw new IllegalArgumentException("NumberAsString cannot be empty");
        }
    }

    /**
     * Gets the value that is going to be used to find numbers in the index. This has transformation performed on it
     * to bring back a normalised value as an integer.
     * <p/>
     * If the number provided starts with "1800" this is also stripped from the value to match
     *
     * @return the full value that can be matched for a word.
     */
    public Integer getMatchingValue() {
        return matchingValue;
    }

    /**
     * Returns the list of matches for the number provided at construction.
     * <p/>
     * Only full-word matches will be returned if any are found. In the case that no full-word matches are found the
     * functionality falls back to finding multi-word matches, or multi-word matches separated by one of the original
     * numbers in the value to match.
     */
    public Set<String> findMatches() {
        final Set<String> matches = new HashSet<>();
        matches.addAll(findFullWordMatches());

        if (matches.isEmpty()) {
            matches.addAll(findMultiWordMatches());
        }
        return matches;
    }

    private Set<String> findFullWordMatches() {
        if (getMatchingValue() != null) {
            return generateOutput(wordIndex.search(getMatchingValue()));
        }
        return new HashSet<>();
    }

    /**
     * Finds matching options for:
     * <ul>
     * <li>word-word</li>
     * <li>digit-word</li>
     * <li>word-digit-word</li>
     * <li>word-digit</li>
     * </ul>
     *
     * @return the set of words complete for output including the 1-800 prefix.
     */
    private Set<String> findMultiWordMatches() {
        final Set<String> matches = new HashSet<>();
        if (getMatchingValue() == null) {
            return matches;
        }

        final String matchingValueAsString = String.valueOf(getMatchingValue());

        for (int i = 1; i < matchingValueAsString.length(); i++) {
            final String firstPart = matchingValueAsString.substring(0, i);
            final Set<String> firstPartWords = findMatches(firstPart);
            if (!hasValues(firstPartWords)) {
                continue;
            }
            //generate any output for word-word option
            matches.addAll(generateOutput(firstPartWords, findMatches(matchingValueAsString.substring(i))));

            //get the current character to see whether there are matches when excluding it from a search for the remainder.
            final char currentDigit = matchingValueAsString.charAt(i);
            if (isLastCharacter(matchingValueAsString, i)) {
                //generate any output for word-digit option
                matches.addAll(generateOutput(firstPartWords, currentDigit));
            } else {
                //generate any output for word-digit-word option
                matches.addAll(generateOutput(firstPartWords, currentDigit, findMatches(matchingValueAsString.substring(i + 1))));
            }
        }
        //generate any output for digit-word option
        matches.addAll(generateOutput(matchingValueAsString.charAt(0), findMatches(matchingValueAsString.substring(1))));
        return matches;
    }

    private Set<String> findMatches(final String searchValue) {
        return wordIndex.search(Integer.parseInt(searchValue));
    }

    private boolean isLastCharacter(final String matchingValueAsString, final int i) {
        return i == matchingValueAsString.length() - 1;
    }

    private boolean hasValues(final Set<String> words) {
        return words != null && !words.isEmpty();
    }

    private boolean hasValues(final Set<String> words, final Set<String> moreWords) {
        return hasValues(words) && hasValues(moreWords);
    }

    private Set<String> generateOutput(final Set<String> firstWords, final Set<String> secondWords) {
        final Set<String> output = new HashSet<>();
        if (hasValues(firstWords, secondWords)) {
            for (final String firstWord : firstWords) {
                for (final String secondPartWord : secondWords) {
                    output.add(TextUtils.joinAs1800Number(firstWord, secondPartWord));
                }
            }
        }
        return output;
    }

    private Set<String> generateOutput(final Set<String> firstWords, final char firstChar, final Set<String> secondWords) {
        final Set<String> output = new HashSet<>();
        if (hasValues(firstWords, secondWords)) {
            for (final String firstWord : firstWords) {
                for (final String secondWord : secondWords) {
                    output.add(TextUtils.joinAs1800Number(firstWord, String.valueOf(firstChar), secondWord));
                }
            }
        }
        return output;
    }

    private Set<String> generateOutput(final Set<String> words, final char number) {
        final Set<String> output = new HashSet<>();
        if (hasValues(words)) {
            for (final String firstWord : words) {
                output.add(TextUtils.joinAs1800Number(firstWord, String.valueOf(number)));
            }
        }
        return output;
    }

    private Set<String> generateOutput(final char number, final Set<String> words) {
        final Set<String> output = new HashSet<>();
        if (hasValues(words)) {
            for (final String word : words) {
                output.add(TextUtils.joinAs1800Number(String.valueOf(number), word));
            }
        }
        return output;
    }

    private Set<String> generateOutput(final Set<String> words) {
        final Set<String> output = new HashSet<>();
        if (hasValues(words)) {
            for (final String word : words) {
                output.add(TextUtils.joinAs1800Number(word));
            }
        }
        return output;
    }
}
