package com.aconex;

import java.io.Console;
import java.util.Arrays;

/**
 * Converts numbers to strings based on phone number --> letter conversion equivalents.
 */
public class PhoneNumberConverter {
    private static final String EXIT_COMMAND = "exit";

    public static void main(String[] args) {
        final PhoneNumberConverter phoneNumberConverter = new PhoneNumberConverter();
        phoneNumberConverter.process(args);
    }

    private void process(String[] args) {

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
                    //TODO: convert the number
                    System.out.println("You entered: " + number);
                }
            }
        } else {
            System.out.println("Files to process: " + Arrays.toString(args));
        }
    }
}
