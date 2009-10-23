package org.mix3.twitter_bot_gae_wicket.auth;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.mix3.twitter_bot_gae_wicket.service.SlimService;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;

public class Redirect extends WebPage{
	@Inject
	protected SlimService slimservice;
	
	public Redirect(){
		UserService userService = UserServiceFactory.getUserService();
		String userid = userService.getCurrentUser().getUserId();
		slimservice.register(userid);
		
    	if(!continueToOriginalDestination()){
    		setRedirect(true);
    		throw new RestartResponseException(getApplication().getHomePage());
    	}
	}
}
