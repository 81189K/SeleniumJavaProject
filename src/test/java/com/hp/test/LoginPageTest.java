package com.hp.test;

import com.hp.pages.LoginPage;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hp.base.BaseClass;
import com.hp.pages.HomePage;

public class LoginPageTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage();
        homePage = new HomePage();
    }

    @Test
    public void testValidLogin() {
        loginPage.login(prop.getProperty("username"), prop.getProperty("password"));
        Assert.assertTrue(homePage.isAdminTabVisible(), "Admin tab should be visible after valid login");
        Assert.assertTrue(homePage.isOrangeHRMLogoVisible(), "OrangeHRM logo should be visible after valid login");
        homePage.logout();
    }

    @Test
    public void testInvalidLogin() {
        loginPage.login("invalidUser", "invalidPass");
        String expectedErrorMessage = "Invalid credentials";
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid login");
        Assert.assertEquals(loginPage.getErrorMessageText(), expectedErrorMessage, " Expected Error message text should match expected value");
    }

}
