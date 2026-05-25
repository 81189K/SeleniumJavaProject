package com.hp.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseClass {
	
	protected static Properties prop; // will be initialized only once, because tagged with @BeforeSuite. Therefore, make it static so that it will be loaded once and will be available at class for all tests to access.
	protected WebDriver driver;
	
	/***
	 * load the configuration file
	 */
	@BeforeSuite
	public void loadConfig() throws IOException {
		prop = new Properties();
		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
		prop.load(fis); //loads the file
	}
	

	
	
	@BeforeMethod
	public void setup() throws IOException {
		System.out.println("Setting up WebDriver for: "+ this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(2);
	}
	
	/***
	 * Initialize the WebDriver based on browser defined in config.properties file
	 */
	private void launchBrowser() {
		String browser = prop.getProperty("browser");
		if(browser.equalsIgnoreCase("chrome")) {
			driver = new ChromeDriver();
		} else if(browser.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();
		} else if(browser.equalsIgnoreCase("edge")) {
			driver = new EdgeDriver();
		} else {
			throw new IllegalArgumentException("Browser not supported: "+ browser); //throw exception or make one browser as default.
		}
	}
	
	/***
	 * Configure Browser settings such as
	 * Implicit Wait
	 * Maximize window
	 * Navigate to URL
	 */
	private void configureBrowser() {
		//Implicit Wait
		int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
		
		//Maximize the driver
		driver.manage().window().maximize();
		
		//Navigate to URL
		try {
			driver.get(prop.getProperty("url"));
		} catch (Exception e) {
			System.out.println("Failed to navigate to the URL: " + e.getMessage());
		}
	}
	
	@AfterMethod
	public  void teardown() {
		if(driver != null) {
			try {
				driver.quit();
			} catch (Exception e) {
				System.out.println("Failed to quit the driver: " + e.getMessage());
			}
		}
	}

	/***
	 * Static wait for pause
	 * @param seconds
	 */
	public void staticWait(int seconds) {
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}
