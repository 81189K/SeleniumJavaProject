package com.hp.test;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hp.base.BaseClass;
import com.hp.pages.HomePage;
import com.hp.pages.LoginPage;

public class HomePageTest extends BaseClass{

    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage();
        homePage = new HomePage();
    }

    @Test
    public void verifyHomePageElements() {
        loginPage.login(prop.getProperty("username"), prop.getProperty("password"));
        Assert.assertTrue(homePage.isAdminTabVisible(), "Admin tab should be visible after valid login");
        Assert.assertTrue(homePage.isOrangeHRMLogoVisible(), "OrangeHRM logo should be visible after valid login");
        homePage.logout();
    }
}
