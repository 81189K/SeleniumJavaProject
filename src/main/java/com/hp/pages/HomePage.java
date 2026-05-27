package com.hp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.hp.actiondriver.ActionDriver;
import com.hp.base.BaseClass;

public class HomePage {
    
    private ActionDriver actionDriver;

    //Initialize ActionDriver by passing WebDriver instance
    public HomePage(WebDriver driver) {
        // this.actionDriver = new ActionDriver(driver);
        this.actionDriver = BaseClass.getActionDriver(); // Get the shared ActionDriver instance from BaseClass
    }

    //By locators for home page elements
    private By adminTab = By.xpath("//span[normalize-space()='Admin']");
    private By userIDButton = By.className("oxd-userdropdown-name");
    private By logoutButton = By.xpath("//a[normalize-space()='Logout']");
    private By orangeHRMLogo = By.xpath("//img[@alt='client brand banner']");

    //Method to verify if Admin tab is visible
    public boolean isAdminTabVisible() {
        return actionDriver.isElementDisplayed(adminTab);
    }

    //Method to verify if OrangeHRM logo is visible
    public boolean isOrangeHRMLogoVisible() {
        return actionDriver.isElementDisplayed(orangeHRMLogo);
    }

    //Method to perform logout action
    public void logout() {
        actionDriver.click(userIDButton);
        actionDriver.click(logoutButton);
    }
}   
