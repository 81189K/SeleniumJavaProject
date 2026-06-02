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
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.hp.actiondriver.ActionDriver;
import com.hp.utilities.ExtentManager;
import com.hp.utilities.LoggerManager;

public class BaseClass {
	
	protected static Properties prop; // will be initialized only once, because tagged with @BeforeSuite. Therefore, make it static so that it will be loaded once and will be available at class for all tests to access.
	// protected WebDriver driver;
	// private static ActionDriver actionDriver; // Declare ActionDriver as static to ensure it is shared across all instances of BaseClass and initialized only once after WebDriver is set up.
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>(); // Use ThreadLocal to manage WebDriver instances for parallel execution
	private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>(); // Use ThreadLocal to manage ActionDriver instances for parallel execution
	public static final Logger logger = LoggerManager.getLogger(BaseClass.class); // Initialize Log4j logger for BaseClass
	protected ThreadLocal<SoftAssert> softAssert = ThreadLocal.withInitial(SoftAssert::new); // Use ThreadLocal to manage SoftAssert instances for parallel execution

	/***
	 * load the configuration file
	 */
	@BeforeSuite
	public void loadConfig() throws IOException {
		prop = new Properties();
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config.properties");
		prop.load(fis); //loads the file
		logger.info("config.properties file loaded successfully");

		//Start the Extent Report before any tests are run
		// ExtentManager.getReporter(); // implemented in TestListner.onStart() to ensure that the report is initialized before any test starts, and that the same report instance is used across all tests.
	}
	
	@BeforeMethod
	public void setup() throws IOException {
		logger.info("Setting up WebDriver for: "+ this.getClass().getSimpleName() + " in thread {}: " + Thread.currentThread().threadId());
		launchBrowser();
		configureBrowser();
		// staticWait(2);
		// logger.info("WebDriver initialized and Browser maximized");

		//ActionDriver should be initialized after WebDriver is set up
		actionDriver.set(new ActionDriver());
		// logger.info("ActionDriver instance initialized for thread: " + Thread.currentThread().threadId());
	}
	
	/***
	 * Initialize the WebDriver based on browser defined in config.properties file
	 */
	private void launchBrowser() {
		String browser = prop.getProperty("browser");
		if(browser.equalsIgnoreCase("chrome")) {
			//ChromOptions
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless=new"); // Run Chrome in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU for headless mode
			options.addArguments("--window-size=1920,1080"); // Set window size

			// ADD THESE TWO LINES TO OVERRIDE HIGH-DPI SCALING:
			options.addArguments("--force-device-scale-factor=1");
			options.addArguments("--high-dpi-support=1");

			options.addArguments("--disable-notifications"); // Disable browser notifications
			options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
			options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource-limited environments

			// driver = new ChromeDriver();
			driver.set(new ChromeDriver(options)); // Set the WebDriver instance for the current thread using ThreadLocal
			ExtentManager.registerDriver(getDriver());
			logger.info("ChromeDriver initialized");
		} else if(browser.equalsIgnoreCase("firefox")) {
			//FirefoxOptions
			FirefoxOptions options = new FirefoxOptions();

			// 1. Native Firefox Headless mode
			options.addArguments("-headless"); 

			// 2. Set the native window size at boot (Firefox uses a single string flag)
			options.addArguments("--window-size=1920,1080"); 

			// 3. FIX HIGH-DPI/DEVICE SCALING IN FIREFOX:
			// This is the direct equivalent of --force-device-scale-factor=1
			options.addPreference("layout.css.devPixelsPerPx", "1.0");

			// 4. Disable notifications and web push alerts
			options.addPreference("dom.webnotifications.enabled", false);
			options.addPreference("dom.push.enabled", false);

			// 5. Performance tuners for resource-limited CI/CD environments
			options.addPreference("browser.tabs.remote.autostart", true);
			options.addPreference("layers.acceleration.disabled", true); // Replaces --disable-gpu

			// Instantiate the Firefox driver with your tailored options & set to current thread
			// driver = new FirefoxDriver();
			driver.set(new FirefoxDriver(options));
			ExtentManager.registerDriver(getDriver());
			logger.info("FirefoxDriver initialized");
		} else if(browser.equalsIgnoreCase("edge")) {
			EdgeOptions options = new EdgeOptions();
			options.addArguments("--headless"); // Run Edge in headless mode
			options.addArguments("--disable-gpu"); // Disable GPU acceleration
			options.addArguments("--window-size=1920,1080"); // Set window size
			
			// ADD THESE TWO LINES TO OVERRIDE HIGH-DPI SCALING:
			options.addArguments("--force-device-scale-factor=1");
			options.addArguments("--high-dpi-support=1");
			
			options.addArguments("--disable-notifications"); // Disable pop-up notifications
			options.addArguments("--no-sandbox"); // Needed for CI/CD
			options.addArguments("--disable-dev-shm-usage"); // Prevent resource-limited crashes

			// driver = new EdgeDriver();
			driver.set(new EdgeDriver()); 
			ExtentManager.registerDriver(getDriver());
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
		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
		
		//Maximize the driver
//		getDriver().manage().window().maximize();
		// Force sync the layout dimensions post-boot
		getDriver().manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
		
		//Navigate to URL
		String URL = prop.getProperty("url");
		try {
			// logger.info("Navigating to URL: " + URL);
			logger.info("Launching application");
			getDriver().get(URL);
		} catch (Exception e) {
			logger.error("Launching application in thread {}: " + Thread.currentThread().threadId() + " with error: " + e.getMessage());
			throw new RuntimeException("Launching application in thread {}: " + Thread.currentThread().threadId() + " with error: " + e.getMessage(), e); //ensure test fails when navigation action fails
		}
	}
	
	@AfterMethod
	public void teardown() {
		if(getDriver() != null) {
			try {
				getDriver().quit();
			} catch (Exception e) {
				logger.error("Failed to quit the driver: " + e.getMessage());
				throw new RuntimeException("Failed to quit the driver: " + e.getMessage(), e);
			}
		}
		logger.info("Teardown completed for: "+ this.getClass().getSimpleName());
		// driver = null; // Set driver to null after quitting to avoid stale reference issues in subsequent tests
		// actionDriver = null; // Set actionDriver to null to ensure it will be re-initialized in the next test setup
		driver.remove(); // Remove the WebDriver instance for the current thread from ThreadLocal to avoid memory leaks
		actionDriver.remove(); // Remove the ActionDriver instance for the current thread from ThreadLocal to avoid memory leaks

		// ExtentManager.endTest();  // handled in TestListener.onFinish()
	}

	/***
	 * Getter Method for Properties
	 * @return Properties instance
	 */
	public static Properties getProp() {
		return prop;
	}

	/***
	 * Getter Method for WebDriver to be used in ActionDriver class
	 * @return WebDriver instance
	 */
	public static WebDriver getDriver() {
		if (driver.get() == null) {
			logger.error("WebDriver is not initialized");
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver.get(); // Return the WebDriver instance for the current thread
	}

	/***
	 * Getter Method for ActionDriver to be used in ActionDriver class
	 * @return ActionDriver instance
	 */
	public static ActionDriver getActionDriver() {
		if (actionDriver.get() == null) {
			throw new IllegalStateException("ActionDriver is not initialized"  + Thread.currentThread().threadId());
		}
		return actionDriver.get(); // Return the ActionDriver instance for the current thread
	}

	/***
	 * Getter Method for SoftAssert to be used in test classes for assertions
	 * @return SoftAssert instance
	 */
	public SoftAssert getSoftAssert() {
		return softAssert.get(); // Return the SoftAssert instance for the current thread
	}

	/***
	 * Static wait for pause
	 * @param seconds
	 */
	public static void staticWait(int seconds) {
		logger.info("Performing static wait for {} seconds", seconds);
		LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
	}
}
