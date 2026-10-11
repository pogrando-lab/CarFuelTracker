package com.carfueltracker.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Basic unit test to verify build and test harness configuration for Stage 1.
 */
public class ExampleUnitTest {

    @Test
    public void projectInitialization_isCorrect() {
        assertEquals("Initial stage assertion", 4, 2 + 2);
    }

    @Test
    public void mainActivity_classExists() {
        assertNotNull(MainActivity.class);
    }
}
