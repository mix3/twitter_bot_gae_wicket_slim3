package org.mix3.twitter_bot_gae_wicket.page.cron;

import org.apache.wicket.PageParameters;

import twitter4j.TwitterException;

public class DirectQueue extends MyAbstractQueuePage{
	public DirectQueue(PageParameters parameters){
		super(parameters);
		
		try {
			slimservice.directQueue(
					parameters.getString("username"),
					parameters.getString("message")
			);
			info("OK!");
		} catch (TwitterException e) {
			error("Twitter API Error...");
		}
	}
}
