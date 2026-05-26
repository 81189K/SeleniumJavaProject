package com.hp.actiondriver;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hp.base.BaseClass;

public class ActionDriver {
	
	private WebDriver driver;
	private WebDriverWait wait;
	
	public ActionDriver(WebDriver driver) {
		this.driver = driver;
		int explicitWait = Integer.parseInt(BaseClass.getProp().getProperty("explicitWait"));
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
		System.out.println("WebDriver instance initialized in ActionDriver");
	}
	
	
	//Method to click an element
	public void click(By by) {
		try {
			waitForElementToBeClickable(by);
			driver.findElement(by).click();
			System.out.println("Clicked on element: " + by.toString());
		} catch (Exception e) {
			System.out.println("Unable to click element: "+ e.getMessage());
		}
	}
	
	//Method to enter text into an input field
	public void enterText(By by, String value) {
		try {
			waitForElementToBeVisible(by);
			WebElement inputFieldElement = driver.findElement(by);
			inputFieldElement.clear();
			inputFieldElement.sendKeys(value);
			System.out.println("Entered text '" + value + "' into element: " + by.toString());
		} catch (Exception e) {
			System.out.println("Unable to enter the value: "+ e.getMessage());
		}
	}

	//Method to get text from an element
	public String getText(By by) {
		try {
			waitForElementToBeVisible(by);
			return driver.findElement(by).getText();
		} catch (Exception e) {
			System.out.println("Unable to get text: "+ e.getMessage());
			return null;
		}
	}

	//Method to compare text of an element with expected value
	public boolean compareText(By by, String expectedValue) {
		try {
			String actualText = getText(by);
			return actualText.equals(expectedValue);
		} catch (Exception e) {
			System.out.println("Unable to compare text: "+ e.getMessage());
			return false;
		}
	}

	//Method to check if an element is displayed
	public boolean isElementDisplayed(By by) {
		try {
			waitForElementToBeVisible(by);
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			System.out.println("Unable to check if element is displayed: "+ e.getMessage());
			return false;
		}
	}

	//Scroll to an element
	public void scrollToElement(By by) {
		try {
			WebElement element = driver.findElement(by);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		} catch (Exception e) {
			System.out.println("Unable to scroll to element: "+ e.getMessage());
		}
	}

	//Wait for page to load completely
	public void waitForPageToLoad() {
    try {
        wait.until(webDriver -> "complete".equals(
            ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
        ));
    } catch (Exception e) {
        System.out.println("Page did not load completely: " + e.getMessage());
    }
}
	
	//Wait for element to be clickable
	public void waitForElementToBeClickable(By by) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (Exception e) {
			System.out.println("Element is not clickable: "+ e.getMessage());
		}
	}
	
	//Wait for element to be visible
	public void waitForElementToBeVisible(By by) {
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			System.out.println("Element is not visible: "+ e.getMessage());
		}
	}

}
