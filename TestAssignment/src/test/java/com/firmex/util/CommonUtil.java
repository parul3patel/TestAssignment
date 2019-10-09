package com.firmex.util;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.firmex.util.Constants.Config;
import com.firmex.util.Constants.FileType;
import com.firmex.util.Constants.Language;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class CommonUtil {

	public static WebDriver driver;
	public static Logger log = Logger.getLogger(CommonUtil.class);
	public static String browser;
	public static String environment;
	public static String current_page;
	private static Language _language;
	private static String orFileName;
	public static String device;
	public static  String reportName = "";

	public static final int avgWaitTime = 10;

	private String getResourcePath(String file){
		return new File(getClass().getClassLoader().getResource(file).getFile()).getAbsolutePath();
	}
	public static String getResourceLocation(String file){
		return new CommonUtil().getResourcePath(file);
	}

	public static String getDataObject(String header, String key) throws Throwable{
		return getPropertyValue(header,key,FileType.Data);
	}
	public static String getConfigObject(Config header, String key) throws Throwable{
		return getPropertyValue(header.toString(),key,FileType.Config);
	}

	public static void setPreferredLanguage() throws Exception{
		try{
			//by default english
			Language language = Language.English;
			
			log.info("Setting preferred language starts..");
				log.info("Preferred language is English");
				language = Language.English;
			
			_language = language;
			log.info("Preferred Language set to "+language.toString());
		}catch(Throwable e){
			log.error("Error during setting preferred language :: "+e.getLocalizedMessage());
			throw new Exception("Error during setting preferred language");
		}finally{
			log.info("Fetching preferred language ends..");
		}
	}

	public static void setORfileName() throws Exception{
		try{
			String filename = "";
			log.info("setORfileName starts..");
			if(getPreferredLanguage().equals(Language.English)){
				filename = "firmex_EN.ini";
			}else if(getPreferredLanguage().equals(Language.French)){
				filename = "firmex_FR.ini";
			}
			log.info("setting or filename as "+filename);
			orFileName = filename;
		}catch(Exception e){
			log.error("Error during setting OR filename :: "+e.getLocalizedMessage());
			throw new Exception("Error during setting OR filename");
		}finally{
			log.info("setORfileName ends..");
		}
	}

	public static void setEnvAndDeviceAndBrowser() throws Exception{
		try{
			String _device = "web";
			String _browser = "ch";
			String _environment = "qa";
			log.info("set device & browser starts..");
					
			log.info("device name set to : "+_device);
			device = _device.trim();
			
			log.info("browser name set to : "+_browser);
			browser = _browser.trim();
	
			log.info("environment name set to : "+_environment);
			environment = _environment.trim();

		}catch(Throwable e){
			log.error("Error during setting device name :: "+e.getLocalizedMessage());
			throw new Exception("Error during setting device name");
		}finally{
			log.info("set device & browser ends..");
		}
	}

	public static Language getPreferredLanguage(){
		return _language;
	}

	public static String getPropertyValue(String header, String key, FileType indicator) throws InvalidFileFormatException, Exception{
		log.info("fetching value starts for ["+header+"] >> "+key);
		String value = "";
		Ini ini = null;
		try{
			if(indicator.toString().equalsIgnoreCase("OR")){
				ini = new Ini(new File(new File("").getAbsolutePath().toString()+"\\objectRepository\\"+orFileName));
				value = ini.get(header, key);
			}else if(indicator.toString().equalsIgnoreCase("Data")){
				ini = new Ini(new File(new File("").getAbsolutePath().toString()+"\\Dataset\\DataFile.ini"));
				value = ini.get(header, key);
			}else if(indicator.toString().equalsIgnoreCase("Config")){
				ini = new Ini(new File(new File("").getAbsolutePath().toString()+"\\config.ini"));
				value = ini.get(header, key);
			}
			if(value == null || value.equals("")){
				value = searchORval(ini, header, key);
				if(value.equals("")){
					log.error("Null value found for ["+header+"] >> "+key);
					return "";
				}
			}
		}catch(Exception e){
			log.warn("Something went wrong during fetching value");
			log.error(e.getLocalizedMessage());
			throw e;
		}finally{
			log.info("fetching value ends for ["+header+"] >> "+key);
		}
		return value;
	}
	private static String searchORval(Ini file, String header, String key) throws Exception{
		String value = "";
		try{
			log.info("searching while ignoring case starts..");
			boolean flag = false;
			boolean flag2 = false;
			String headerName = "";
			for (String sectionName: file.keySet()) {
				if(sectionName.equalsIgnoreCase(header)){
					headerName = sectionName;
					flag = true;
					break;
				}
			}
			if(!flag){
				log.warn(header+" not found in file.");
				return "";
			}else{
				Section section = file.get(headerName);
				for (String optionKey: section.keySet()) {
					if(optionKey.equalsIgnoreCase(key)){
						flag2 = true;
						value = section.get(optionKey);
						break;
					}
				}
				if(!flag2){
					log.warn(key+" not found under "+headerName);
					return "";
				}
			}
		}catch(Exception e){
			log.error("Error :: "+e.getMessage());
		}finally{
			log.info("searching while ignoring case ends..");
		}
		return value;
	}

	public static void endTest(String jirareportid){
		try {
			log.info("TEST EXECUTIONS ENDED FOR TEST :::: "+jirareportid);
			log.info("Begin ending the test..");
			if(browser.equalsIgnoreCase("ch") || browser.equalsIgnoreCase("chrome")){
				Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe").waitFor();
			}else if(browser.equalsIgnoreCase("ff") || browser.equalsIgnoreCase("firefox")){
				Runtime.getRuntime().exec("taskkill /f /im geckodriver.exe").waitFor();
			}else if(browser.equalsIgnoreCase("ie") || browser.equalsIgnoreCase("internet explorer")){
				Runtime.getRuntime().exec("taskkill /f /im iedriverserver.exe").waitFor();
			}
		}catch (Exception e) {
			log.warn("Error during taskkill inside endTest()");
			log.error("\n\n"+e.getMessage()+"\n\n");
		}finally{
			log.info("Ending the test..");
		}
	}


	private static File getLatestFilefromDir(String dirPath){
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return null;
		}

		File lastModifiedFile = files[0];
		for (int i = 1; i < files.length; i++) {
			if (lastModifiedFile.lastModified() < files[i].lastModified()) {
				lastModifiedFile = files[i];
			}
		}
		return lastModifiedFile;
	}
	public static void initiateReporting() throws Exception{
		String basepath = CommonUtil.getLatestFilefromDir(new File("").getAbsolutePath().toString()+"\\output").getAbsolutePath();
		log.info(basepath);
		reportName = basepath+"\\report.html";
	}

	public static String getInputText(String input){
		log.info("Start :: getInputText");
		String value = "";
		try{
			if(input == null){
				return value;
			}else if(input.equalsIgnoreCase("<blank>")){
				return value;
			}else if(input.equalsIgnoreCase("<space>")){
				value = " ";
			}else{
				value = input.trim().replace("<SPACE>", " ").replace("<space>", " ").replace("<blank>", "").replace("<BLANK>", "").replace("<HASH>","#").replace("<hash>","#").replace("<#>", "#").replace("<VERTICAL>","|").replace("<vertical>","|");
			}	
		}catch(Exception e){
			log.error("Something went wrong :: getInputText");
		}finally{
			log.info("End :: getInputText");
		}
		return value;
	}

	public static String retrieveLocator(String object, String key) throws Exception{
		log.info("Start retrieveLocator..");
		String locator = "";
		String val_cp = getPropertyValue(current_page, key, FileType.OR);
		String val_cmn = getPropertyValue("Common", key, FileType.OR);
		if(val_cp!=null && !val_cp.isEmpty()){
			locator = val_cp;
		}else if(val_cmn!=null && !val_cmn.isEmpty()){
			locator = val_cmn;
		}else{
			Ini ini = new Ini(new File(new File("").getAbsolutePath().toString()+"\\objectRepository\\"+orFileName));
			for (String sectionName: ini.keySet()) {
				if(sectionName.equalsIgnoreCase(object)){
					if(!getPropertyValue(sectionName, key, FileType.OR).isEmpty()){
						locator = getPropertyValue(sectionName, key, FileType.OR);
						break;
					}
				}
			}
		}
		if(locator.isEmpty()){
			throw new Exception("locator was not found in OR file");
		}
		log.info("End retrieveLocator..");
		return locator;
	}

	public static void throwCustomException(String message) throws Throwable {
		boolean flag = false;
		String screenShotName = "";

		if(driver != null && !driver.toString().contains("null")){
			//Thread.sleep(3000L);
			screenShotName = getScreenShot();
			flag = true;
		}
		if(message.length()>55){
			if(flag){
				message = message.substring(0,55)+"....[refer logs for more details ... | ScreenShot :: "+screenShotName+"]";
			}else{
				message = message.substring(0,55)+"....[refer logs for more details ...]";
			}
		}else{
			if(flag){
				message = message + " ...[ScreenShot :: "+screenShotName+"]";
			}
		}
		Throwable t = new Throwable(message.toUpperCase());

		StackTraceElement[] trace = new StackTraceElement[] {
				new StackTraceElement("firmex","TEST",getPreferredLanguage().toString(),-1)
		};

		t.setStackTrace(trace);
		throw t;
	}

	public static String getScreenShot(){
		String screenShotName = "";
		String full_screenShotName = "";
		String timer = Instant.now().toEpochMilli()+"";
		try{
			log.info("getScreenShot started");
			if(current_page != null && !current_page.isEmpty()){
				screenShotName = current_page+"_"+timer+".png";
				full_screenShotName = current_page+"_"+timer+"_Full"+".png";
			}else{
				screenShotName = "ErrorPage"+"_"+timer+".png";
				full_screenShotName = "ErrorPage"+"_"+timer+"_Full"+".png";
			}
			File screenshotfolder = new File(new File("").getAbsolutePath().toString()+"\\ScreenShot");
			if(!screenshotfolder.exists()){
				log.info("screenshot folder does not exist... creating one.");
				boolean flag = false;
				int attempt = 0;
				while(attempt<3 && !flag){
					flag = screenshotfolder.mkdir();
					attempt++;
				}
			}
			
			File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			File destFile = new File(new File("").getAbsolutePath().toString()+"\\ScreenShot\\"+screenShotName);
			if(destFile.exists()) {
				destFile.delete();
			}
			Files.copy(srcFile.toPath(), destFile.toPath());
			
			
			try {
				Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(2000)).takeScreenshot(driver);
				ImageIO.write(screenshot.getImage(),"PNG", new File(new File("").getAbsolutePath().toString()+"\\ScreenShot\\"+full_screenShotName));
			}catch(Exception e) {
				log.info(e.getMessage());
			}
			
		}catch(Throwable e){
			log.warn(e.getMessage());
			log.error("Failed to take a screenShot");
		}finally {
			log.info("getScreenShot ends");
		}
		return screenShotName;
	}

	public static List<String> getIdentifierAndLocator(WebElement we) {
		log.info("start : getIdentifierAndLocator");
		List<String> list = new ArrayList<String>();
		String text = we.toString();
		try{
			if(text.contains("xpath:")){
				log.info("locator xpath");
				list.add("xpath");
				String[] parts = text.trim().split("xpath:");
				String locator = parts[1];
				int endIndex = locator.length() - 1;
				list.add(locator.substring(0, endIndex).toString().trim());
			}else if(text.contains("css selector:")){
				log.info("locator css");
				list.add("css");
				String[] parts = text.trim().split("css selector:");
				String locator = parts[1];
				int endIndex = locator.length() - 1;
				list.add(locator.substring(0, endIndex).toString().trim());
			}
		}catch(Exception e){
			log.error(e.getMessage());
		}finally{
			log.info("end : getIdentifierAndLocator");
		}
		return list;

	}

}
