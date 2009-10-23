package org.mix3.twitter_bot_gae_wicket.page.cron;

import twitter4j.TwitterException;

public class Replies extends MyAbstractCronPage{
	public Replies(){
		super();
		
		try {
			slimservice.replies();
			info("OK!");
		} catch (TwitterException e) {
			error("Twitter API Error...");
		}
	}
}
