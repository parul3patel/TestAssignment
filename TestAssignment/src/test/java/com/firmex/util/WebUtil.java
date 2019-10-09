package com.firmex.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.firmex.util.Constants.FileType;

public class WebUtil {

	private static Logger log = Logger.getLogger(WebUtil.class);

		public static void initializeBrowser(String browserName) throws Throwable{
		log.info("initializeBrowser starts...");
		try{
			log.info("here");
			CommonUtil.setEnvAndDeviceAndBrowser();
				if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("ch")) {
					log.info("here1");
					System.setProperty("webdriver.chrome.driver", CommonUtil.getResourceLocation("browserDriver/chromedriver.exe"));
					ChromeOptions chromeOptions = new ChromeOptions();
					chromeOptions.addArguments("disable-infobars");
					chromeOptions.addArguments("start-maximized");
					CommonUtil.driver = new ChromeDriver(chromeOptions);
					//log.info(language);
					CommonUtil.setPreferredLanguage();
	
					CommonUtil.setORfileName();
				} else if (browserName.equalsIgnoreCase("firefox") || browserName.equalsIgnoreCase("ff")) {
					System.setProperty("webdriver.gecko.driver", CommonUtil.getResourceLocation("browserDriver/geckodriver.exe"));
					FirefoxProfile profile = new FirefoxProfile();
					FirefoxOptions ffOptions = new FirefoxOptions();
					ffOptions.setProfile(profile);
					CommonUtil.driver = new FirefoxDriver(ffOptions);
					CommonUtil.driver.manage().window().maximize();
				} else if(browserName.equalsIgnoreCase("ie") || browserName.equalsIgnoreCase("internet explorer")) {
					System.setProperty("webdriver.ie.driver", CommonUtil.getResourceLocation("browserDriver/IEDriverServer32.exe"));
					DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
					caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
					//CommonUtil.driver = new InternetExplorerDriver(caps);
				} else if(browserName.equalsIgnoreCase("ie64") || browserName.equalsIgnoreCase("internet explorer64")) {
					System.setProperty("webdriver.ie.driver", CommonUtil.getResourceLocation("browserDriver/IEDriverServer.exe"));
					DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
					caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				//	CommonUtil.driver = new InternetExplorerDriver(caps);
				}
		}catch(Exception e){
			log.error("Error during browser initialization");
			log.error("\n\n"+e.getMessage()+"\n\n");
			throw new Exception("Error during browser initialization [refer logs]");
		}finally{
			log.info("initializeBrowser ends...");
		}		
	}

	public static void mouseMove(WebElement element) throws Exception{
		log.info("START :: Moving cursor to webelement");
		try{
			((JavascriptExecutor)CommonUtil.driver).executeScript("arguments[0].scrollIntoView();", element);
		}catch(Exception e){
			log.error("Something went wrong : mouseMove");
			log.error("\n\n"+e.getMessage()+"\n\n");
		}finally{
			log.info("END :: Moving cursor to webelement");
		}
	}

	
	public static void launchApplication(String url) throws Throwable{
		log.info("launchBrowser starts...");
		try{
			CommonUtil.driver.navigate().to(url.trim());

		}catch(Exception e){
			log.error("Error ocured during application launch");
			log.error("\n\n"+e.getMessage()+"\n\n");
			throw new Exception("Error ocured during application launch [refer logs]");
		}finally{
			log.info("launchBrowser ends...");
		}

	}

	public static String getPageObject(String header, String key) throws Throwable{
		return CommonUtil.getPropertyValue(header,key,FileType.OR);
	}

	public static WebElement getWebElement(String locator) throws Exception{
		try{
			log.info("Getting WebElement Starts..");
			if(locator == null || locator.trim().isEmpty()){
				log.error("Invalid Locator. No value for locator found");
				throw new Exception("Invalid Locator. No value for locator found");
			}
			WebElement we = null;
			//Thread.sleep(3000L);
			we = getWebElementUsingXpath(locator);
			if(we == null){
				log.warn("Could not locate element using xpath. Trying css..");
				we = getWebElementUsingCss(locator);
				if(we == null){
					log.warn("Could not locate element using xpath or css");
					log.error("Could not locate. Please update OR file. Invalid locator : "+locator);
					//throw new Exception("Invalid locator. Please update OR file.");
				}
			}
			return we;
		}catch(Exception e){
			log.warn("Error in getWebElement : "+e.getMessage());
			throw e;
		}finally{
			log.info("Getting WebElement Ends..");
		}
	}
	private static WebElement getWebElementUsingXpath(String locator){
		try{
			WebDriverWait wait = new WebDriverWait(CommonUtil.driver, CommonUtil.avgWaitTime);
			WebElement we = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(locator)));
			return we;
		}catch(Exception e){
			log.warn("getWebElementUsingXpath -->"+e.getMessage());
			return null;
		}
	}
	private static WebElement getWebElementUsingCss(String locator){
		try{
			WebDriverWait wait = new WebDriverWait(CommonUtil.driver, CommonUtil.avgWaitTime);
			WebElement we = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(locator)));
			return we;
		}catch(Exception e){
			log.warn("getWebElementUsingCss -->"+e.getMessage());
			return null;
		}
	}

	public static List<WebElement> getAllWebElements(String locator) throws Exception{
		try{
			log.info("Getting all WebElements Starts..");
			if(locator == null || locator.trim().isEmpty()){
				log.error("Invalid Locator. No value for locator found");
				throw new Exception("Invalid Locator. No value for locator found");
			}
			List<WebElement> weList = null;
			//Thread.sleep(3000L);
			weList = getAllWebElementsUsingXpath(locator);
			if(weList == null){
				log.warn("Could not locate element using xpath. Trying css..");
				weList = getAllWebElementsUsingCss(locator);
				if(weList == null){
					log.warn("Could not locate element using xpath or css");
					log.error("Could not locate. Please update OR file. Invalid locator : "+locator);
					//throw new Exception("Invalid locator. Please update OR file.");
					/**Returning an empty list**/
					return new ArrayList<WebElement>();
				}
			}
			return weList;
		}catch(Exception e){
			log.warn(e.getMessage());
			throw e;
		}finally{
			log.info("Getting all WebElements Ends..");
		}
	}
	private static List<WebElement> getAllWebElementsUsingXpath(String locator){
		try{
			WebDriverWait wait = new WebDriverWait(CommonUtil.driver, CommonUtil.avgWaitTime);
			List<WebElement> we = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(locator)));
			return we;
		}catch(Exception e){
			log.warn("getAllWebElementsUsingXpath -->"+e.getMessage());
			return null;
		}
	}
	private static List<WebElement> getAllWebElementsUsingCss(String locator){
		try{
			WebDriverWait wait = new WebDriverWait(CommonUtil.driver, CommonUtil.avgWaitTime);
			List<WebElement> we = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(locator)));
			return we;
		}catch(Exception e){
			log.warn("getAllWebElementsUsingCss -->"+e.getMessage());
			return null;
		}
	}



}
