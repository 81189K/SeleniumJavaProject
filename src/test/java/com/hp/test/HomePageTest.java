package com.hp.test;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.hp.base.BaseClass;
import com.hp.pages.HomePage;
import com.hp.pages.LoginPage;

public class HomePageTest extends BaseClass{

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
    public void verifyHomePageElements() {
        loginPage.get().login(prop.getProperty("username"), prop.getProperty("password"));
        Assert.assertTrue(homePage.get().isAdminTabVisible(), "Admin tab should be visible after valid login");
        Assert.assertTrue(homePage.get().isOrangeHRMLogoVisible(), "OrangeHRM logo should be visible after valid login");
        homePage.get().logout();
    }

    @AfterMethod
    public void tearDownPages() {
        loginPage.remove();
        homePage.remove();
    }
}
