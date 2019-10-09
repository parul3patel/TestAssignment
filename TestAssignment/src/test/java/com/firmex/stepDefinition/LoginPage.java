package com.firmex.stepDefinition;

import org.apache.log4j.Logger;
import com.firmex.util.CommonUtil;
import cucumber.api.java.en.Then;

public class LoginPage {
	
	private static Logger log = Logger.getLogger(LoginPage.class);
	private static String pagename = "Login";

	@Then("^user is on Login page$")
	public void verify_loginpage() throws Throwable{
		Thread.sleep(6000L); //This wait is needed to allow Login page to load fully
		verify_Loginpage_web();
	}

	public void verify_Loginpage_web() throws Throwable{
		log.info("Start : Verify Login page loads|web");
		try{
			//Thread.sleep(3000L);
			CommonUtil.current_page = pagename;
			log.info("\n\nCurrent page will be considered as :: "+CommonUtil.current_page+"\n\n");
			}catch(Throwable e){
			log.warn("Error encountered during Loginpage loading");
			log.error("Error details : "+e.getLocalizedMessage());
			CommonUtil.throwCustomException(e.getMessage());
		}finally{
			log.info("End : Verify Login page loads|web");
		}		
	}
}
