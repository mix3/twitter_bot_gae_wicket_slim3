package org.mix3.twitter_bot_gae_wicket.page.cron;

import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class Token extends MyAbstractCronPage{
	public Token(){
		add(new FeedbackPanel("feedback"));
		slimservice.token();
	}
}
