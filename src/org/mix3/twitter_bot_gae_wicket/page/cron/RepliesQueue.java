package org.mix3.twitter_bot_gae_wicket.page.cron;

import org.apache.wicket.PageParameters;

import twitter4j.TwitterException;

public class RepliesQueue extends MyAbstractQueuePage {
	public RepliesQueue(PageParameters parameters){
		super(parameters);
		
		try {
			if(!parameters.getString("statusid").equals("-1") || !parameters.getString("username").equals("")){
				slimservice.repliesQueue(
						parameters.getString("username"),
						parameters.getString("message"),
						parameters.getString("statusid")
				);
			}
			info("Post OK!");
		} catch (TwitterException e) {
			error("Twitter API Error...");
		}
	}
}
