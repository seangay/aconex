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

    public static String joinAs1800Number(String... elements) {
        final StringBuilder builder = new StringBuilder("1-800");

        for (String element : elements) {
            builder.append("-").append(element);
        }

        return builder.toString();
    }
}
