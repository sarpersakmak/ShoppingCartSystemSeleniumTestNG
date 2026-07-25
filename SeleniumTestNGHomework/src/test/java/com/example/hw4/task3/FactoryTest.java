package com.example.hw4.task3;

import com.example.hw4.util.BrowserProvider;
import org.testng.annotations.Factory;

/**
 * Demonstrates TestNG's {@code @Factory} by creating one test instance per browser.
 */
public final class FactoryTest {

    @Factory
    public Object[] createBrowserTestInstances() {
        String[] browsers = BrowserProvider.configuredBrowsers();
        Object[] instances = new Object[browsers.length];"

        for (int index = 0; index < browsers.length; index++) {
            instances[index] = new FactoryBrowserTitleTest(browsers[index]);
        }
        return instances;
    }
}
