package com.hp.test;

import com.hp.pages.LoginPage;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hp.base.BaseClass;
import com.hp.pages.HomePage;

public class LoginPageTest extends BaseClass {

    // private LoginPage loginPage;
    // private HomePage homePage;

    // @BeforeMethod
    // public void setupPages() {
    //     loginPage = new LoginPage();
    //     homePage = new HomePage();
    // }

    // 1. Declare Page Objects as ThreadLocal
    private ThreadLocal<LoginPage> loginPage = new ThreadLocal<>();
    private ThreadLocal<HomePage> homePage = new ThreadLocal<>();

    @BeforeMethod
    public void setupPages() {
        // 2. Set the instances for the current thread
        loginPage.set(new LoginPage());
        homePage.set(new HomePage());
    }

    @Test
    public void testValidLogin() {
        loginPage.get().login(prop.getProperty("username"), prop.getProperty("password"));
        Assert.assertTrue(homePage.get().isAdminTabVisible(), "Admin tab should be visible after valid login");
        Assert.assertTrue(homePage.get().isOrangeHRMLogoVisible(), "OrangeHRM logo should be visible after valid login");
        homePage.get().logout();
    }

    @Test
    public void testInvalidLogin() {
        loginPage.get().login("invalidUser", "invalidPass");
        String expectedErrorMessage = "Invalid credentials";
        Assert.assertTrue(loginPage.get().isErrorMessageDisplayed(), "Error message should be displayed for invalid login");
        Assert.assertEquals(loginPage.get().getErrorMessageText(), expectedErrorMessage, " Expected Error message text should match expected value");
    }

    @AfterMethod
    public void tearDownPages() {
        loginPage.remove();
        homePage.remove();
    }

}
