package com.hp.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class ConditionalRetry implements IRetryAnalyzer {

    private int attempt = 0;
    private static final int MAX_RETRY = 2;

    @Override
    public boolean retry(ITestResult result) {

        Throwable t = result.getThrowable();

        boolean isTransient =
                t instanceof org.openqa.selenium.StaleElementReferenceException ||
                t instanceof org.openqa.selenium.TimeoutException ||
                t instanceof org.openqa.selenium.ElementClickInterceptedException;

        if (isTransient && attempt < MAX_RETRY) {
            attempt++;
            return true;
        }

        return false;
    }
}