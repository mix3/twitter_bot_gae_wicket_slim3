package org.mix3.twitter_bot_gae_wicket.page.cron;

import org.apache.wicket.PageParameters;

import twitter4j.TwitterException;

public class TokenQueue extends MyAbstractQueuePage{
	public TokenQueue(PageParameters parameters){
		super(parameters);
		
		try {
			slimservice.tokenQueue(
					parameters.getString("userid"),
					parameters.getString("access_token"),
					parameters.getString("access_token_secret")
			);
		} catch (TwitterException e) {
			error("Twitter API Error...");
		}
	}
}
