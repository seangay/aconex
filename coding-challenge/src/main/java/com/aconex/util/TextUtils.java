package com.aconex.util;

/**
 * Utility class to centralise some of the input parsing functions required.
 */
public final class TextUtils {
    private TextUtils() {
    }

    /**
     * Removes all whitespace and punctuation from the provided text.
     * <p/>
     * If the provided value is all punctuation and whitespace then null will be returned rather than an empty string.
     */
    public static String stripRedundantCharacters(final String value) {
        if (value != null) {
            final String replacedValue = value.replaceAll("\\W", "");
            return (replacedValue.isEmpty()) ? null : replacedValue;
        }
        return null;
    }

    /**
     * Joins together the elements provided as the 1-800-elements value. The prefix of "1-800" is always added to the
     * response.
     * <p/>
     * All other elements are joined by a "-".
     */
    public static String joinAs1800Number(String... elements) {
        final StringBuilder builder = new StringBuilder("1-800");

        for (String element : elements) {
            builder.append("-").append(element);
        }
        return builder.toString();
    }

    /**
     * Convenience to not have to worry about checking null checks for String value verification.
     */
    public static boolean isEmpty(final String value) {
        return (value == null || value.isEmpty() || value.trim().isEmpty());
    }
}
