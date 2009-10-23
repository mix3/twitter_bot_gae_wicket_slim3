package org.mix3.twitter_bot_gae_wicket.page.cron;

import twitter4j.TwitterException;

public class Post extends MyAbstractCronPage{
	public Post(){
		super();
		
		try {
			slimservice.post();
			info("Post OK!");
		} catch (TwitterException e) {
			error("Twitter API Error...");
		}
	}
}
