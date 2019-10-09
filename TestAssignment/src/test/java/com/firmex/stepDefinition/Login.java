package com.firmex.stepDefinition;

import com.firmex.util.CommonUtil;
import com.firmex.util.WebUtil;

import org.apache.log4j.Logger;
import cucumber.api.java.en.Given;
import com.firmex.util.Constants.Config;

public class Login {

	private static Logger log = Logger.getLogger(Login.class);
	
	@Given("^user launched firmex Application$")
	public void launchfirmex() throws Throwable {
		try {
			log.info("Start :: Launch firmex Application");
			//log.info("CommonUtil.browser.trim()"+CommonUtil.browser.trim());
			WebUtil.initializeBrowser("ch");
			String env = CommonUtil.environment;
			WebUtil.launchApplication(CommonUtil.getConfigObject(Config.environment, env));
		}catch(Exception e) {
			log.warn("Error encountered during firmex launch");
			log.error("Error details : "+e.getLocalizedMessage());
			CommonUtil.throwCustomException(e.getMessage());
		}finally {
			log.info("End :: Launch firmex Application");
		}
	}
}
