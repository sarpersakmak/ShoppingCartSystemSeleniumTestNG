package com.example.hw4.main;

import org.testng.TestNG;

import java.util.Collections;

/**
 * Convenience runner for presentations:
 * - Runs TestNG suite (Tasks 1-3 + previous shopping cart tests)
 * - Then runs the MainProjectRunner (Google+ChatGPT) interactively
 */
public class MainRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Running TestNG suite (see console for results) ===");
        TestNG testng = new TestNG();
        testng.setTestSuites(Collections.singletonList("testng.xml"));
        testng.run();

        System.out.println("\n=== Running Main Project (Google + ChatGPT) ===");
        MainProjectRunner.main(args);
    }
}
