package com.hp.actiondriver;

import static com.hp.base.BaseClass.getDriver;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.Status;
import com.hp.base.BaseClass;
import com.hp.utilities.ExtentManager;

public class ActionDriver {

	//NOTE: works perfectly fine when running tests sequentially (one after another), it completely breaks your framework the moment you enable parallel test execution
	
//	private WebDriver driver; // refer NOTE above for why we are not using this instance variable
	private WebDriverWait wait;
	public static final Logger logger = BaseClass.logger; // Use the same logger instance from BaseClass for logging
	
	public ActionDriver(/*WebDriver driver*/) { // refer NOTE above for why we are not passing WebDriver instance in constructor
		// this.driver = driver; // refer NOTE above for why we are not using this instance variable
		// wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // refer NOTE above for why we are not initializing WebDriverWait here

		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(getDriver(), Duration.ofSeconds(explicitWait)); // use getDriver() to access the WebDriver instance from BaseClass, ensures that the same WebDriver instance is used across all page classes and tests, and that ActionDriver is properly initialized with the WebDriver instance after it is set up in BaseClass.
		// logger.info("WebDriver instance initialized in ActionDriver");  // refer NOTE
	}
	
	
	//Method to click an element
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		try {
			waitForElementToBeClickable(by);
			applyBorder(by, "blue"); // Apply blue border to the element before clicking for better visibility in screenshots and debugging
			getDriver().findElement(by).click();
			logger.info("Clicked on " + elementDescription);
			ExtentManager.logStep("Clicked on " + elementDescription); // Log the click action to the current test in the report with element description
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to click on " + elementDescription + ": " , e);
			ExtentManager.logStepFailure("Unable to click on " + elementDescription + ": " + e);
			throw new RuntimeException("Unable to click on " + elementDescription + ": " + e.getMessage(), e); //ensure test fails when click action fails
		}
	}

	// Method to enter masked text into an input field
	public void enterText(By by, String value, boolean maskInLogs) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "blue");
			WebElement inputFieldElement = getDriver().findElement(by);
			inputFieldElement.clear();
			inputFieldElement.sendKeys(value);
			String maskedValue = maskInLogs ? "*".repeat(value.length()) : value;
			logger.info("Entered text '" + maskedValue + "' into " + getElementDescription(by));
			ExtentManager.logStep("Entered text '" + maskedValue + "' into " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to enter text into " + getElementDescription(by) + ": " , e);
			ExtentManager.logStepFailure("Unable to enter text into " + getElementDescription(by) + ": " + e);
			throw new RuntimeException("Unable to enter text into " + getElementDescription(by) + ": " + e.getMessage(), e); //ensure test fails when enter text action fails
		}
	}	

	//Method to enter text into an input field
	public void enterText(By by, String value) {
		enterText(by, value, false); // Call the overloaded method with maskInLogs set to false by default
	}

	//Method to get text from an element
	public String getText(By by) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "blue");
			String text = getDriver().findElement(by).getText();
			logger.info("Retrieved text '" + text + "' from " + getElementDescription(by));
			ExtentManager.logStep("Retrieved text '" + text + "' from " + getElementDescription(by));
			return text;
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to get text from " + getElementDescription(by) + ": " , e);
			ExtentManager.logStepFailure("Unable to get text from " + getElementDescription(by) + ": " + e);
			throw new RuntimeException("Unable to get text from " + getElementDescription(by) + ": " + e.getMessage(), e); //ensure test fails when get text action fails
		}
	}

	//Method to compare text of an element with expected value
	public boolean compareText(By by, String expectedValue) {
		try {
			String actualText = getText(by);
			if(actualText.equals(expectedValue)) {
				applyBorder(by, "green");
				logger.info("Text comparison passed for " + getElementDescription(by) + ": Expected = '" + expectedValue + "', Actual = '" + actualText + "'");
				ExtentManager.logStepWithScreenshot("Text comparison passed for " + getElementDescription(by) + ": Expected = '" + expectedValue + "', Actual = '" + actualText + "'");
			} else {
				applyBorder(by, "red");
				logger.error("Text comparison failed for " + getElementDescription(by) + ": Expected = '" + expectedValue + "', Actual = '" + actualText + "'");
				ExtentManager.logStepFailure("Text comparison failed for " + getElementDescription(by) + ": Expected = '" + expectedValue + "', Actual = '" + actualText + "'");
			}
			return actualText.equals(expectedValue);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to compare text: "+ e.getMessage(), e);
			ExtentManager.logStepFailure("Unable to compare text: " + e);
			throw new RuntimeException("Unable to compare text: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when compare text action fails
		}
	}

	//Method to check if an element is displayed
	public boolean isElementDisplayed(By by) {
		try {
			waitForElementToBeVisible(by);
			applyBorder(by, "blue");
			boolean isDisplayed = getDriver().findElement(by).isDisplayed();
			logger.info(getElementDescription(by) + (isDisplayed?" is Displayed":" is NOT displayed"));
			ExtentManager.logStep(getElementDescription(by) + (isDisplayed?" is Displayed":" is NOT displayed"));
			return isDisplayed;
		} catch (Exception e) {
			logger.error("Unable to check if element is displayed: ", e);
			ExtentManager.logStepFailure("Unable to check if element is displayed: " + e);	
			throw new RuntimeException("Unable to check if element is displayed: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when check display action fails
		}
	}

	//Scroll to an element
	public void scrollToElement(By by) {
		try {
			WebElement element = getDriver().findElement(by);
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
			applyBorder(by, "blue");
			logger.info("Scrolled to " + getElementDescription(by));
			ExtentManager.logStep("Scrolled to " + getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to scroll to element: ", e);
			ExtentManager.logStepFailure("Unable to scroll to element: " + e);
			throw new RuntimeException("Unable to scroll to element: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when scroll action fails
		}
	}

	//Wait for page to load completely
	public void waitForPageToLoad() {
		try {
			wait.until(webDriver -> "complete".equals(
					((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
			logger.info("Page loaded completely");
			ExtentManager.logStep("Page loaded completely");
		} catch (Exception e) {
			logger.error("Page did not load completely: " , e);
			ExtentManager.logStepFailure("Page did not load completely: " + e);
			throw new RuntimeException("Page did not load completely: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when page load action fails
		}
	}
	
	//Wait for element to be clickable
	public void waitForElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("Exception occurred while waiting for element to be clickable: " + getElementDescription(by), e);
			ExtentManager.logStepFailure("Exception occurred while waiting for element to be clickable: " + getElementDescription(by) + " | Error: " + e);
			throw new RuntimeException("Exception occurred while waiting for element to be clickable: " + e.getMessage(), e);
		}
	}
	
	//Wait for element to be visible
	public void waitForElementToBeVisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("Exception occurred while waiting for element to be visible: " + getElementDescription(by), e);
			ExtentManager.logStepFailure("Exception occurred while waiting for element to be visible: " + getElementDescription(by) + " | Error: " + e);
			throw new RuntimeException("Exception occurred while waiting for element to be visible: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when wait for visible action fails
		}
	}

	public String getElementDescription(By by) {
		if (by == null) {
			IllegalArgumentException e = new IllegalArgumentException("Locator cannot be null");
			logger.error("Locator cannot be null: " , e);
			ExtentManager.logStepFailure("Locator cannot be null: " + e);
			throw new RuntimeException("Locator cannot be null: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when locator is null
		}

		try {
			WebElement element = getDriver().findElement(by);

			String name = element.getDomAttribute("name");
			if (isNotBlank(name))
				return "Element with name: " + name;

			String id = element.getDomAttribute("id");
			if (isNotBlank(id))
				return "Element with id: " + id;

			String placeholder = element.getDomAttribute("placeholder");
			if (isNotBlank(placeholder))
				return "Element with placeholder: " + placeholder;

			String text = element.getText();
			if (isNotBlank(text))
				return "Element with text: " + truncateString(text, 30);	

			String className = element.getDomAttribute("class");
			if (isNotBlank(className))
				return "Element with class: " + className;
			
			String altText = element.getDomAttribute("alt");
			if (isNotBlank(altText))
				return "Element with alt text: " + altText;

		} catch (NoSuchElementException | StaleElementReferenceException e) {
			logger.error("Unable to locate element for description: " , e);
			ExtentManager.logStepFailure("Unable to locate element for description: " + e);
			throw new RuntimeException("Unable to locate element for description: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when element is not found for description
		} catch (Exception e) {
			logger.error("Error while getting element description: " , e);
			ExtentManager.logStepFailure("Error while getting element description: " + e);
			throw new RuntimeException("Error while getting element description: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when any unexpected error occurs while getting element description
		}
		return "Element located by: " + by.toString(); // Fallback to locator description if no attributes are available
	}
	

	//Utility Method to check a String is NOT null or empty
	private static boolean isNotBlank(String value) {
		return value != null && !value.trim().isEmpty();
	}

	//Utility Method to truncate long String
	private static String truncateString(String value, int maxLength) {
		if (value != null && value.length() > maxLength) {
			return value.substring(0, maxLength)+"...";
		}
		return value;
	}
	
	//Utility Method to apply Border to an element (for debugging purposes)
	private void applyBorder(By by, String color) {
		try {
			WebElement element = getDriver().findElement(by);
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].style.border='3px solid " + color + "'", element);
			// logger.info("Applied " + color + " border to " + getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to apply border to element: ", e);
			ExtentManager.logStep(Status.WARNING, "Unable to apply border to element: " + e);
		}
	}

}