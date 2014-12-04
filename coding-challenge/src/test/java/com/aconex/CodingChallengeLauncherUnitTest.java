package com.aconex;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.mockito.Mockito;

/**
 * Test case for testing the command line launcher.
 */
public class CodingChallengeLauncherUnitTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    private CodingChallengeLauncher getMockedLauncher() {
        return new CodingChallengeLauncher(Mockito.mock(PhoneNumberConverter.class));
    }

    @Test
    public void testSystemExitsWhenNoArgsAreProvidedAsThereIsNoConsoleForTesting() {
        exit.expectSystemExitWithStatus(1);
        getMockedLauncher().processArgs(null);
    }

    @Test
    public void testSystemExitValueWhenArgsAreProvided() {
        exit.expectSystemExitWithStatus(0);
        getMockedLauncher().processArgs(new String[]{ "argumentZero" });
    }
}
