package com.example.hw4.main;

import org.testng.TestNG;

import java.util.Collections;

/**
 * Optional presentation runner that executes the TestNG suite first and then,
 * when {@code -DrunResearch=true} is supplied, starts the research application.
 */
public final class MainRunner {

    private MainRunner() {
        // Application entry-point class; prevent instantiation.
    }

    public static void main(String[] args) {
        System.out.println("=== Running TestNG suite ===");

        TestNG testNg = new TestNG();
        testNg.setTestSuites(Collections.singletonList("testng.xml"));
        testNg.run();

        if (testNg.hasFailure()) {
            System.err.println("The TestNG suite completed with failures.");
        }

        boolean runResearch = Boolean.parseBoolean(System.getProperty("runResearch", "false"));
        if (runResearch) {
            System.out.println("\n=== Running automated research project ===");
            MainProjectRunner.main(args);
        }
    }
}
