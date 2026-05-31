package com.hp.test;

import com.hp.pages.LoginPage;
import com.hp.utilities.DataProviderClass;
import com.hp.utilities.ExtentManager;

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

    @Test(dataProvider = "validLoginData", dataProviderClass = DataProviderClass.class)
    public void testValidLogin(String username, String password) {
        // ExtentManager.startTest("testValidLogin"); // Start the Extent Report for this test method --handled in TestListener.onTestStart()
        ExtentManager.logStep("Performing login with valid credentials"); // Log the login step to the current test in the report
        // login using credentials from config.properties file
        // loginPage.get().login(prop.getProperty("username"), prop.getProperty("password"));

        //login using credentials from data provider
        loginPage.get().login(username, password);
        ExtentManager.logStep("Login action performed, verifying home page elements");
        Assert.assertTrue(homePage.get().isAdminTabVisible(), "Admin tab should be visible after valid login");
        Assert.assertTrue(homePage.get().isOrangeHRMLogoVisible(), "OrangeHRM logo should be visible after valid login");
        ExtentManager.logStepWithScreenshot("Home page elements verified successfully");
        ExtentManager.logStep("Performing logout");
        homePage.get().logout();
        ExtentManager.logStep("Logout action performed successfully");
    }

    @Test(dataProvider = "inValidLoginData", dataProviderClass = DataProviderClass.class)
    public void testInvalidLogin(String username, String password) {
        // ExtentManager.startTest("testValidLogin"); // Start the Extent Report for this test method --handled in TestListener.onTestStart()
        ExtentManager.logStep("Performing login with invalid credentials"); // Log the login step to the current test in the report
        loginPage.get().login(username, password);
        String expectedErrorMessage = "Invalid credentials";
        Assert.assertTrue(loginPage.get().isErrorMessageDisplayed(), "Error message should be displayed for invalid login");
        Assert.assertEquals(loginPage.get().getErrorMessageText(), expectedErrorMessage, " Expected Error message text should match expected value");
        ExtentManager.logStep("Invalid login error message verified successfully"); // Log the verification step to the current test in the report
    }

    @AfterMethod
    public void tearDownPages() {
        loginPage.remove();
        homePage.remove();
    }

}
