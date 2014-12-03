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
        this.wordIndex = wordIndex;
        if (wordIndex == null) {
            throw new IllegalArgumentException("Word Index cannot be null");
        }

        if (numberAsString == null || numberAsString.isEmpty()) {
            throw new IllegalArgumentException("NumberAsString cannot be empty");
        }

        String strippedNumberAsString = TextUtils.stripRedundantCharacters(numberAsString);

        if (strippedNumberAsString != null) {
            strippedNumberAsString = strippedNumberAsString.replaceFirst("1800", "");

            try {
                matchingValue = Integer.valueOf(strippedNumberAsString);
            } catch (NumberFormatException e) {
                matchingValue = null;
            }
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
        final HashSet<String> matches = new HashSet<>();

        //get any full work matches from the index
        final Set<String> fullWordMatches = findFullWordMatches();
        if (!fullWordMatches.isEmpty()) {
            matches.addAll(fullWordMatches);
        }

        if (matches.isEmpty()) {
            final Set<String> multiWordMatches = findMultiWordMatches();
            if (!multiWordMatches.isEmpty()) {
                matches.addAll(multiWordMatches);
            }
        }
        return matches;
    }

    private Set<String> findFullWordMatches() {
        if (getMatchingValue() != null) {
            Set<String> fullWordMatches = wordIndex.search(getMatchingValue());

            if (fullWordMatches != null) {
                return generateOutput(fullWordMatches);
            }
        }
        return new HashSet<>();
    }

    /**
     * This is a hairy method but I couldn't see a better way of dealing with the multitude of options. The number needs
     * to be split in various ways and matches need to be found for each.
     * <p/>
     * Options for matching are:
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
        final HashSet<String> matches = new HashSet<>();
        if (getMatchingValue() != null) {
            String matchingValueAsString = String.valueOf(getMatchingValue());

            for (int i = 1; i < matchingValueAsString.length(); i++) {
                final String firstPart = matchingValueAsString.substring(0, i);

                final Set<String> firstPartWords = wordIndex.search(Integer.parseInt(firstPart));
                if (firstPartWords != null && !firstPartWords.isEmpty()) {
                    //see if the second part contains words that may work.
                    final String secondPart = matchingValueAsString.substring(i);
                    final Set<String> secondPartWords = wordIndex.search(Integer.parseInt(secondPart));
                    if (secondPartWords != null && !secondPartWords.isEmpty()) {
                        //now we have matches for the first and second part matching so generate the output for them.
                        matches.addAll(generateOutput(firstPartWords, secondPartWords));
                    }

                    //the final thing to cover off is the digit being "untouched" either in the middle or at the end of the number
                    char currentDigit = matchingValueAsString.charAt(i);
                    if (i != matchingValueAsString.length() - 1) {
                        //only check this if the last part actually matches something
                        final String thirdPart = matchingValueAsString.substring(i + 1);
                        final Set<String> thirdPartWords = wordIndex.search(Integer.parseInt(thirdPart));
                        if (thirdPartWords != null && !thirdPartWords.isEmpty()) {
                            //now we have matches for the first and second part matching so generate the output for them.
                            matches.addAll(generateOutput(firstPartWords, currentDigit, thirdPartWords));
                        }
                    } else {
                        //this covers off the last digit being the one that remains
                        matches.addAll(generateOutput(firstPartWords, currentDigit));
                    }
                }
            }

            //finally cover off the case where the first number is the "untouched one". It isn't done in the loop as it is a specific case.
            char firstDigit = matchingValueAsString.charAt(0);
            final String secondPart = matchingValueAsString.substring(1);
            final Set<String> secondPartWords = wordIndex.search(Integer.parseInt(secondPart));
            if (secondPartWords != null && !secondPartWords.isEmpty()) {
                matches.addAll(generateOutput(firstDigit, secondPartWords));
            }
        }
        return matches;
    }

    private Set<String> generateOutput(final Set<String> firstPartWords, final Set<String> secondPartWords) {
        final HashSet<String> output = new HashSet<>();
        for (String firstPartWord : firstPartWords) {
            for (String secondPartWord : secondPartWords) {
                output.add(TextUtils.joinAs1800Number(firstPartWord, secondPartWord));
            }
        }
        return output;
    }

    private Set<String> generateOutput(final Set<String> firstPartWords, char interimDigit, final Set<String> secondPartWords) {
        final HashSet<String> output = new HashSet<>();
        for (String firstPartWord : firstPartWords) {
            for (String secondPartWord : secondPartWords) {
                output.add(TextUtils.joinAs1800Number(firstPartWord, String.valueOf(interimDigit), secondPartWord));
            }
        }
        return output;
    }

    private Set<String> generateOutput(final Set<String> firstPartWords, char interimDigit) {
        final HashSet<String> output = new HashSet<>();
        for (String firstPartWord : firstPartWords) {
            output.add(TextUtils.joinAs1800Number(firstPartWord, String.valueOf(interimDigit)));
        }
        return output;
    }

    private Set<String> generateOutput(char interimDigit, final Set<String> firstPartWords) {
        final HashSet<String> output = new HashSet<>();
        for (String firstPartWord : firstPartWords) {
            output.add(TextUtils.joinAs1800Number(String.valueOf(interimDigit), firstPartWord));
        }
        return output;
    }

    private Set<String> generateOutput(final Set<String> matches) {
        final HashSet<String> output = new HashSet<>();

        for (String match : matches) {
            output.add(TextUtils.joinAs1800Number(match));
        }
        return output;
    }
}
