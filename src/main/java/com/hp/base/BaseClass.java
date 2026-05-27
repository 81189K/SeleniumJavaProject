package com.hp.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.hp.actiondriver.ActionDriver;
import com.hp.utilities.LoggerManager;

public class BaseClass {
	
	protected static Properties prop; // will be initialized only once, because tagged with @BeforeSuite. Therefore, make it static so that it will be loaded once and will be available at class for all tests to access.
	protected WebDriver driver;
	private static ActionDriver actionDriver; // Declare ActionDriver as static to ensure it is shared across all instances of BaseClass and initialized only once after WebDriver is set up.
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class); // Initialize Log4j logger for BaseClass
	/***
	 * load the configuration file
	 */
	@BeforeSuite
	public void loadConfig() throws IOException {
		prop = new Properties();
		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
		prop.load(fis); //loads the file
		logger.info("config.properties file loaded successfully");
	}
	

	
	
	@BeforeMethod
	public void setup() throws IOException {
		logger.info("Setting up WebDriver for: "+ this.getClass().getSimpleName());
		launchBrowser();
		configureBrowser();
		staticWait(2);
		logger.info("WebDriver initialized and Browser maximized");

		//Initialize actionDriver only once after WebDriver is initialized and configured
		//Singleton pattern to ensure only one instance of ActionDriver is created and shared across all page classes. This way, we can avoid multiple instances of ActionDriver being created for each page class and instead have a single instance that is initialized once and shared across all page classes.
		if(actionDriver == null) {
			actionDriver = new ActionDriver(driver);
			logger.info("ActionDriver instance initialized in BaseClass setup method");
		}
	}
	
	/***
	 * Initialize the WebDriver based on browser defined in config.properties file
	 */
	private void launchBrowser() {
		String browser = prop.getProperty("browser");
		if(browser.equalsIgnoreCase("chrome")) {
			driver = new ChromeDriver();
			logger.info("ChromeDriver initialized");
		} else if(browser.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();
			logger.info("FirefoxDriver initialized");
		} else if(browser.equalsIgnoreCase("edge")) {
			driver = new EdgeDriver();
			logger.info("EdgeDriver initialized");
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
			logger.error("Failed to navigate to the URL: " + e.getMessage());
		}
	}
	
	@AfterMethod
	public  void teardown() {
		if(driver != null) {
			try {
				driver.quit();
			} catch (Exception e) {
				logger.error("Failed to quit the driver: " + e.getMessage());
			}
		}
		logger.info("Teardown completed for: "+ this.getClass().getSimpleName());
		driver = null; // Set driver to null after quitting to avoid stale reference issues in subsequent tests
		actionDriver = null; // Set actionDriver to null to ensure it will be re-initialized in the next test setup
	}

	/***
	 * Getter Method for Properties
	 * @return Properties instance
	 */
	public static Properties getProp() {
		return prop;
	}

	/***
	 * Getter Method for ActionDriver to be used in ActionDriver class
	 * @return ActionDriver instance
	 */
	public static ActionDriver getActionDriver() {
		return actionDriver;
	}

	/***
	 * Static wait for pause
	 * @param seconds
	 */
	public void staticWait(int seconds) {
		logger.info("Performing static wait for {} seconds", seconds);
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}
