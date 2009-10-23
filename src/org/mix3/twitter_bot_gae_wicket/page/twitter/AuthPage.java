package org.mix3.twitter_bot_gae_wicket.page.twitter;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.mix3.twitter_bot_gae_wicket.auth.AuthSession;
import org.mix3.twitter_bot_gae_wicket.page.IndexPage;
import org.mix3.twitter_bot_gae_wicket.page.MyAbstractWebPage;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.RequestToken;

@AuthorizeInstantiation({Roles.ADMIN, Roles.USER})
public class AuthPage extends MyAbstractWebPage{
	@SuppressWarnings("serial")
	public AuthPage(PageParameters parameters){
		super(parameters);
		
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
        add(feedback.setOutputMarkupId(true));
        
		Twitter twitter = getTwitter();
		twitter.setOAuthAccessToken(slimservice.getAccessToken(getUser()));
		try {
			twitter.verifyCredentials();
//			System.out.println(twitter.verifyCredentials().getId());
//			System.out.println(twitter.verifyCredentials().getName());
//			System.out.println(twitter.verifyCredentials().getScreenName());
			info("ボットの発言に、あなたの編集したボットの発言内容が反映される状態です。");
		} catch (TwitterException e1) {
			String callback_url = RequestUtils.toAbsolutePath(
					urlFor(CallBackPage.class, new PageParameters()).toString());
			try {
				twitter = getTwitter();
				RequestToken requestToken = twitter.getOAuthRequestToken(callback_url);
				((AuthSession)getSession()).setRequest_token(requestToken.getToken());
				((AuthSession)getSession()).setRequest_token_secret(requestToken.getTokenSecret());
				throw new RestartResponseException(
						new RedirectPage(requestToken.getAuthorizationURL()));
			} catch (TwitterException e2) {
				error("Twitter API Error?");
			}
		}
		
		add(new Form<Void>("form"){
			@Override
			public void onSubmit() {
				slimservice.delAccessToken(getUser());
				setResponsePage(IndexPage.class);
			}
		});
	}

	@Override
	protected String getContentTitle() {
		return "みんながボット - Twitter OAuth";
	}
}
