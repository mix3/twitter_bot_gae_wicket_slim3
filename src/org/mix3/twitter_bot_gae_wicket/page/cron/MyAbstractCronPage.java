package org.mix3.twitter_bot_gae_wicket.page.cron;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.protocol.http.WebRequest;
import org.mix3.twitter_bot_gae_wicket.WicketApplication;
import org.mix3.twitter_bot_gae_wicket.page.IndexPage;
import org.mix3.twitter_bot_gae_wicket.service.SlimService;

import com.google.inject.Inject;

public class MyAbstractCronPage extends WebPage{
	@Inject
	protected SlimService slimservice;
	
	public MyAbstractCronPage(){
		boolean isLocalMode = ((WicketApplication)getApplication()).getConfigurationType().equals(Application.DEVELOPMENT);
		
		HttpServletRequest request = ((WebRequest) RequestCycle.get().getRequest()).getHttpServletRequest();
		if(!isLocalMode && (request.getHeader("X-AppEngine-Cron") == null || !request.getHeader("X-AppEngine-Cron").equals("true"))){
			setRedirect(true);
			throw new RestartResponseException(IndexPage.class);
		}
		
		add(new FeedbackPanel("feedback"));
	}
}
