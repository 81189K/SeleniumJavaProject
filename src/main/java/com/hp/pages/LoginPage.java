package com.hp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.hp.actiondriver.ActionDriver;

public class LoginPage {

    private ActionDriver actionDriver;

    //Initialize ActionDriver by passing WebDriver instance
    public LoginPage(WebDriver driver) {
        this.actionDriver = new ActionDriver(driver);
    }

    //By locators for login page elements
    private By usernameField = By.cssSelector("input[name='username']");
    private By passwordField = By.cssSelector("input[name='password']");
    private By loginButton = By.xpath("//button[normalize-space()='Login']");
    private By errorMessage = By.xpath("//p[text()='Invalid credentials']");
    
    //Method to perform login action
    public void login(String username, String password) {
        actionDriver.enterText(usernameField, username);
        actionDriver.enterText(passwordField, password);
        actionDriver.click(loginButton);
    }

    //Method to check if error message is displayed
    public boolean isErrorMessageDisplayed() {
        return actionDriver.isElementDisplayed(errorMessage);
    }

    //Method to get the error message text
    public String getErrorMessageText() {
        return actionDriver.getText(errorMessage);
    }

    //Method to verify error message text    
    public boolean verifyErrorMessageText(String expectedErrorMessage) {
        return actionDriver.compareText(errorMessage, expectedErrorMessage);
    }
}
