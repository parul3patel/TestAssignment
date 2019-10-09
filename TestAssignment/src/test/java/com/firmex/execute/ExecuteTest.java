package com.firmex.execute;

import cucumber.api.CucumberOptions;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.firmex.util.CommonUtil;
import com.cucumber.listener.ExtentCucumberFormatter;
import cucumber.api.testng.AbstractTestNGCucumberTests;


@CucumberOptions(
		glue= "com.firmex.stepDefinition",
		plugin = {"com.cucumber.listener.ExtentCucumberFormatter"}
		)

public class ExecuteTest extends AbstractTestNGCucumberTests{
	private static Logger log = Logger.getLogger(ExecuteTest.class);
	
	@BeforeClass
	public static void _setup() throws Exception {
		ExtentCucumberFormatter.initiateExtentCucumberFormatter();
		log.info("test");
		/**Language must be set prior to setting orFilename*/
		CommonUtil.setPreferredLanguage();
		CommonUtil.setEnvAndDeviceAndBrowser();
		CommonUtil.setORfileName();
	}

	@AfterClass
	public static void _postRun() throws Exception{
		log.info("test1");
		CommonUtil.initiateReporting();
	}


}

