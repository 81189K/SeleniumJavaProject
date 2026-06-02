package com.hp.actiondriver;

import static com.hp.base.BaseClass.getDriver;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

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
			String originalStyle = applyBorder(by, "blue");
			WebElement inputFieldElement = getDriver().findElement(by);
			inputFieldElement.clear();
			inputFieldElement.sendKeys(value);
			String maskedValue = maskInLogs ? "*".repeat(value.length()) : value;
			logger.info("Entered text '" + maskedValue + "' into " + getElementDescription(by));
			ExtentManager.logStep("Entered text '" + maskedValue + "' into " + getElementDescription(by));
			removeBorder(by, originalStyle); // Remove the border after entering text
		} catch (Exception e) {
			String originalStyle = applyBorder(by, "red");
			logger.error("Unable to enter text into " + getElementDescription(by) + ": " , e);
			ExtentManager.logStepFailure("Unable to enter text into " + getElementDescription(by) + ": " + e);
			removeBorder(by, originalStyle);
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
			String originalStyle = applyBorder(by, "blue");
			String text = getDriver().findElement(by).getText();
			// logger.info("Retrieved text '" + text + "' from " + getElementDescription(by));
			// ExtentManager.logStep("Retrieved text '" + text + "' from " + getElementDescription(by));
			removeBorder(by, originalStyle);
			return text;
		} catch (Exception e) {
			String originalStyle = applyBorder(by, "red");
			logger.error("Unable to get text from " + getElementDescription(by) + ": " , e);
			ExtentManager.logStepFailure("Unable to get text from " + getElementDescription(by) + ": " + e);
			removeBorder(by, originalStyle);
			throw new RuntimeException("Unable to get text from " + getElementDescription(by) + ": " + e.getMessage(), e); //ensure test fails when get text action fails
		}
	}

	//Method to compare text of an element with expected value
	public boolean compareText(By by, String expectedValue) {
		try {
			String actualText = getText(by);
			if(actualText.equals(expectedValue)) {
				String originalStyle = applyBorder(by, "green");
				logger.info("Text comparison passed for " + getElementDescription(by) + ": Expected = '" + expectedValue + "', Actual = '" + actualText + "'");
				ExtentManager.logStepWithScreenshot("Text comparison passed for " + getElementDescription(by) + ": Expected = '" + expectedValue + "', Actual = '" + actualText + "'");
				removeBorder(by, originalStyle);
			} else {
				String originalStyle = applyBorder(by, "red");
				logger.error("Text comparison failed for " + getElementDescription(by) + ": Expected = '" + expectedValue + "', Actual = '" + actualText + "'");
				ExtentManager.logStepFailure("Text comparison failed for " + getElementDescription(by) + ": Expected = '" + expectedValue + "', Actual = '" + actualText + "'");
				removeBorder(by, originalStyle);
			}
			return actualText.equals(expectedValue);
		} catch (Exception e) {
			String originalStyle = applyBorder(by, "red");
			logger.error("Unable to compare text: "+ e.getMessage(), e);
			ExtentManager.logStepFailure("Unable to compare text: " + e);
			removeBorder(by, originalStyle);
			throw new RuntimeException("Unable to compare text: " + e.getMessage(), e); // Rethrow the exception to ensure test fails when compare text action fails
		}
	}

	//Method to check if an element is displayed
	public boolean isElementDisplayed(By by) {
		try {
			waitForElementToBeVisible(by);
			String originalStyle = applyBorder(by, "blue");
			boolean isDisplayed = getDriver().findElement(by).isDisplayed();
			logger.info(getElementDescription(by) + (isDisplayed?" is Displayed":" is NOT displayed"));
			ExtentManager.logStep(getElementDescription(by) + (isDisplayed?" is Displayed":" is NOT displayed"));
			removeBorder(by, originalStyle);
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
			String originalStyle = applyBorder(by, "blue");
			logger.info("Scrolled to " + getElementDescription(by));
			ExtentManager.logStep("Scrolled to " + getElementDescription(by));
			removeBorder(by, originalStyle);
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
	private String applyBorder(By by, String color) {
		try {
			WebElement element = getDriver().findElement(by);
			String originalStyle = element.getAttribute("style");
			//apply border
			((JavascriptExecutor) getDriver())
					.executeScript(
							"arguments[0].style.border=arguments[1]",
							element,
							"3px solid " + color);
			//return original style
			return originalStyle;
		} catch (Exception e) {
			logger.warn("Unable to apply border to {}", getElementDescription(by), e);
			return null;
		}
	}

	private void removeBorder(By by, String originalStyle) {
		try {
			WebElement element = getDriver().findElement(by);
			((JavascriptExecutor) getDriver())
					.executeScript(
							"arguments[0].setAttribute('style', arguments[1])",
							element,
							originalStyle);
		} catch (Exception e) {
			logger.warn("Unable to remove border for {}", getElementDescription(by), e);
		}
	}

	// ===================== Select Methods =====================

	// Method to select a dropdown by visible text
	public void selectByVisibleText(By by, String value) {
		try {
			WebElement element = getDriver().findElement(by);
			new Select(element).selectByVisibleText(value);
			applyBorder(by, "green");
			logger.info("Selected dropdown value: " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown value: " + value, e);
		}
	}

	// Method to select a dropdown by value
	public void selectByValue(By by, String value) {
		try {
			WebElement element = getDriver().findElement(by);
			new Select(element).selectByValue(value);
			applyBorder(by, "green");
			logger.info("Selected dropdown value by actual value: " + value);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown by value: " + value, e);
		}
	}

	// Method to select a dropdown by index
	public void selectByIndex(By by, int index) {
		try {
			WebElement element = getDriver().findElement(by);
			new Select(element).selectByIndex(index);
			applyBorder(by, "green");
			logger.info("Selected dropdown value by index: " + index);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to select dropdown by index: " + index, e);
		}
	}

	// Method to get all options from a dropdown
	public List<String> getDropdownOptions(By by) {
		List<String> optionsList = new ArrayList<>();
		try {
			WebElement dropdownElement = getDriver().findElement(by);
			Select select = new Select(dropdownElement);
			for (WebElement option : select.getOptions()) {
				optionsList.add(option.getText());
			}
			applyBorder(by, "green");
			logger.info("Retrieved dropdown options for " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to get dropdown options: " + e.getMessage());
		}
		return optionsList;
	}

	// ===================== JavaScript Utility Methods =====================

	// Method to click using JavaScript
	public void clickUsingJS(By by) {
		try {
			WebElement element = getDriver().findElement(by);
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
			applyBorder(by, "green");
			logger.info("Clicked element using JavaScript: " + getElementDescription(by));
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to click using JavaScript", e);
		}
	}

	// Method to scroll to the bottom of the page
	public void scrollToBottom() {
		((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(0, document.body.scrollHeight);");
		logger.info("Scrolled to the bottom of the page.");
	}

	// Method to highlight an element using JavaScript
	public void highlightElementJS(By by) {
		try {
			WebElement element = getDriver().findElement(by);
			((JavascriptExecutor) getDriver()).executeScript("arguments[0].style.border='3px solid yellow'", element);
			logger.info("Highlighted element using JavaScript: " + getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to highlight element using JavaScript", e);
		}
	}

	// ===================== Window and Frame Handling =====================

	// Method to switch between browser windows
	public void switchToWindow(String windowTitle) {
		try {
			Set<String> windows = getDriver().getWindowHandles();
			for (String window : windows) {
				getDriver().switchTo().window(window);
				if (getDriver().getTitle().equals(windowTitle)) {
					logger.info("Switched to window: " + windowTitle);
					return;
				}
			}
			logger.warn("Window with title " + windowTitle + " not found.");
		} catch (Exception e) {
			logger.error("Unable to switch window", e);
		}
	}

	// Method to switch to an iframe
	public void switchToFrame(By by) {
		try {
			getDriver().switchTo().frame(getDriver().findElement(by));
			logger.info("Switched to iframe: " + getElementDescription(by));
		} catch (Exception e) {
			logger.error("Unable to switch to iframe", e);
		}
	}

	// Method to switch back to the default content
	public void switchToDefaultContent() {
		getDriver().switchTo().defaultContent();
		logger.info("Switched back to default content.");
	}

	// ===================== Alert Handling =====================

	// Method to accept an alert popup
	public void acceptAlert() {
		try {
			getDriver().switchTo().alert().accept();
			logger.info("Alert accepted.");
		} catch (Exception e) {
			logger.error("No alert found to accept", e);
		}
	}

	// Method to dismiss an alert popup
	public void dismissAlert() {
		try {
			getDriver().switchTo().alert().dismiss();
			logger.info("Alert dismissed.");
		} catch (Exception e) {
			logger.error("No alert found to dismiss", e);
		}
	}

	// Method to get alert text
	public String getAlertText() {
		try {
			return getDriver().switchTo().alert().getText();
		} catch (Exception e) {
			logger.error("No alert text found", e);
			return "";
		}
	}

	// ===================== Browser Actions =====================

	public void refreshPage() {
		try {
			getDriver().navigate().refresh();
			ExtentManager.logStep("Page refreshed successfully.");
			logger.info("Page refreshed successfully.");
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to refresh page");
			logger.error("Unable to refresh page: " + e.getMessage());
		}
	}

	public String getCurrentURL() {
		try {
			String url = getDriver().getCurrentUrl();
			ExtentManager.logStep("Current URL fetched: " + url);
			logger.info("Current URL fetched: " + url);
			return url;
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to fetch current URL" + ": " + "get_current_url_failed");
			logger.error("Unable to fetch current URL: " + e.getMessage());
			return null;
		}
	}

	public void maximizeWindow() {
		try {
			getDriver().manage().window().maximize();
			ExtentManager.logStep("Browser window maximized.");
			logger.info("Browser window maximized.");
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to maximize window"+ ": " + "maximize_window_failed");
			logger.error("Unable to maximize window: " + e.getMessage());
		}
	}

	// ===================== Advanced WebElement Actions =====================

	public void moveToElement(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(getDriver());
			actions.moveToElement(getDriver().findElement(by)).perform();
			ExtentManager.logStep("Moved to element: " + elementDescription);
			logger.info("Moved to element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to move to " + elementDescription + ": move_failed");
			logger.error("Unable to move to element: " + e.getMessage());
		}
	}

	public void dragAndDrop(By source, By target) {
		String sourceDescription = getElementDescription(source);
		String targetDescription = getElementDescription(target);
		try {
			Actions actions = new Actions(getDriver());
			actions.dragAndDrop(getDriver().findElement(source), getDriver().findElement(target)).perform();
			ExtentManager.logStep("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
			logger.info("Dragged element: " + sourceDescription + " and dropped on " + targetDescription);
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to drag and drop from " + sourceDescription + " to " + targetDescription + ": " + "drag_and_drop_failed");
			logger.error("Unable to drag and drop: " + e.getMessage());
		}
	}

	public void doubleClick(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(getDriver());
			actions.doubleClick(getDriver().findElement(by)).perform();
			ExtentManager.logStep("Double-clicked on element: " + elementDescription);
			logger.info("Double-clicked on element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to double-click element" + elementDescription + ": doubleclick_failed");
			logger.error("Unable to double-click element: " + e.getMessage());
		}
	}

	public void rightClick(By by) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(getDriver());
			actions.contextClick(getDriver().findElement(by)).perform();
			ExtentManager.logStep("Right-clicked on element: " + elementDescription);
			logger.info("Right-clicked on element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to right-click element" + elementDescription + ": rightclick_failed");
			logger.error("Unable to right-click element: " + e.getMessage());
		}
	}

	public void sendKeysWithActions(By by, String value) {
		String elementDescription = getElementDescription(by);
		try {
			Actions actions = new Actions(getDriver());
			actions.sendKeys(getDriver().findElement(by), value).perform();
			ExtentManager.logStep("Sent keys to element: " + elementDescription + " | Value: " + value);
			logger.info("Sent keys to element --> " + elementDescription + " | Value: " + value);
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to send keys to " + elementDescription + ": sendkeys_failed");
			logger.error("Unable to send keys to element: " + e.getMessage());
		}
	}

	public void clearText(By by) {
		String elementDescription = getElementDescription(by);
		try {
			getDriver().findElement(by).clear();
			ExtentManager.logStep("Cleared text in element: " + elementDescription);
			logger.info("Cleared text in element --> " + elementDescription);
		} catch (Exception e) {
			ExtentManager.logStepFailure("Unable to clear text in " + elementDescription + ": clear_failed");
			logger.error("Unable to clear text in element: " + e.getMessage());
		}
	}

	// Method to upload a file
	public void uploadFile(By by, String filePath) {
		try {
			getDriver().findElement(by).sendKeys(filePath);
			applyBorder(by, "green");
			logger.info("Uploaded file: " + filePath);
		} catch (Exception e) {
			applyBorder(by, "red");
			logger.error("Unable to upload file: " + e.getMessage());
		}
	}

}