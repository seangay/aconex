package com.aconex;

import java.io.Console;
import java.util.Arrays;

/**
 * Base launcher to handle command line input and main method stuff.
 */
public class CodingChallengeLauncher {
    private static final String EXIT_COMMAND = "exit";
    private PhoneNumberConverter phoneNumberConverter;

    public CodingChallengeLauncher(final PhoneNumberConverter phoneNumberConverter) {
        this.phoneNumberConverter = phoneNumberConverter;
    }

    public static void main(String[] args) {
        new CodingChallengeLauncher(new PhoneNumberConverter()).processArgs(args);
    }

    void processArgs(String[] args) {
        if (args == null || args.length == 0) {
            Console console = System.console();
            if (console == null) {
                System.err.println("No console is available to accept input. Please supply a file containing numbers to convert to words.");
                System.exit(1);
            }
            boolean keepRunning = true;
            while (keepRunning) {
                String number = console.readLine(String.format("Enter a number to find matching words, or '%s' to quit: ", EXIT_COMMAND));
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
}
