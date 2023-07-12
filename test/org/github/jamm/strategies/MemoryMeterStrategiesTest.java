package org.github.jamm.strategies;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import org.github.jamm.MemoryMeter.Guess;

import static org.junit.Assert.*;

public class MemoryMeterStrategiesTest {

    @Test
    public void testInvalidGetStrategyInput() {
        MemoryMeterStrategies strategies = MemoryMeterStrategies.getInstance();
        assertTrue(strategies.hasInstrumentation());
        assertTrue(strategies.hasUnsafe());

        SortedSet<Guess> guesses = new TreeSet<>();
        try {
            strategies.getStrategy(guesses);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The guessSet argument is empty", e.getMessage());
        }
    }
}
