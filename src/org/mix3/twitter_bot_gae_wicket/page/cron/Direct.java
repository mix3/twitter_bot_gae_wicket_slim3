package org.mix3.twitter_bot_gae_wicket.page.cron;

import twitter4j.TwitterException;

public class Direct extends MyAbstractCronPage{
	public Direct(){
		super();
		
		try {
			slimservice.direct();
			info("OK!");
		} catch (TwitterException e) {
			error("Twitter API Error...");
		}
	}
}
