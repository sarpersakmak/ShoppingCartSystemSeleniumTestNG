package com.example.hw4.task3;

import org.testng.annotations.Factory;

/**
 * Task 3:
 * Demonstrates TestNG @Factory by generating test instances for multiple browsers.
 */
public class FactoryTest {

    @Factory
    public Object[] createInstances() {
        return new Object[] {
                new FactoryBrowserTitleTest("chrome"),
                new FactoryBrowserTitleTest("firefox")
        };
    }
}
