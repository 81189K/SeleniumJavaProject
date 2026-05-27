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

import com.hp.base.BaseClass;

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
		logger.info("WebDriver instance initialized in ActionDriver");
	}
	
	
	//Method to click an element
	public void click(By by) {
		String elementDescription = getElementDescription(by);
		try {
			waitForElementToBeClickable(by);
			getDriver().findElement(by).click();
			logger.info("Clicked on " + elementDescription);
		} catch (Exception e) {
			logger.error("Unable to click element: "+ e.getMessage());
		}
	}
	
	//Method to enter text into an input field
	public void enterText(By by, String value) {
		enterText(by, value, false); // Call the overloaded method with maskInLogs set to false by default
	}

	//Method to enter masked text into an input field
	public void enterText(By by,String value,boolean maskInLogs) {
    try {
		waitForElementToBeVisible(by);
		WebElement inputFieldElement = getDriver().findElement(by);
		inputFieldElement.clear();
		inputFieldElement.sendKeys(value);
		String maskedValue = maskInLogs ? "*".repeat(value.length()) : value;
		logger.info("Entered text '" + maskedValue + "' into " + getElementDescription(by));
    } catch (Exception e) {
		logger.error("Unable to enter the value: "+ e.getMessage());
    }
}	

	//Method to get text from an element
	public String getText(By by) {
		try {
			waitForElementToBeVisible(by);
			return getDriver().findElement(by).getText();
		} catch (Exception e) {
			logger.error("Unable to get text: "+ e.getMessage());
			return null;
		}
	}

	//Method to compare text of an element with expected value
	public boolean compareText(By by, String expectedValue) {
		try {
			String actualText = getText(by);
			return actualText.equals(expectedValue);
		} catch (Exception e) {
			logger.error("Unable to compare text: "+ e.getMessage());
			return false;
		}
	}

	//Method to check if an element is displayed
	public boolean isElementDisplayed(By by) {
		try {
			waitForElementToBeVisible(by);
			boolean isDisplayed = getDriver().findElement(by).isDisplayed();
			logger.info(getElementDescription(by) + (isDisplayed?" is Displayed":" is NOT displayed"));
			return isDisplayed;
		} catch (Exception e) {
			logger.error("Unable to check if element is displayed: "+ e.getMessage());
			return false;
		}
	}

	//Scroll to an element
	public void scrollToElement(By by) {
		try {
			WebElement element = getDriver().findElement(by);
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			logger.error("Unable to scroll to element: "+ e.getMessage());
		}
	}

	//Wait for page to load completely
	public void waitForPageToLoad() {
		try {
			wait.until(webDriver -> "complete".equals(
					((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
		} catch (Exception e) {
			logger.error("Page did not load completely: " + e.getMessage());
		}
	}
	
	//Wait for element to be clickable
	public void waitForElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			logger.error("Element is not clickable: "+ e.getMessage());
		}
	}
	
	//Wait for element to be visible
	public void waitForElementToBeVisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			logger.error("Element is not visible: "+ e.getMessage());
		}
	}

	public String getElementDescription(By by) {
		if (by == null) {
			return "locator is null";
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
			logger.error("Unable to locate element for description: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Error while getting element description: " + e.getMessage());
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

}