package org.mix3.twitter_bot_gae_wicket.page.twitter;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.mix3.twitter_bot_gae_wicket.auth.AuthSession;
import org.mix3.twitter_bot_gae_wicket.page.MyAbstractWebPage;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;

@AuthorizeInstantiation({Roles.ADMIN, Roles.USER})
public class CallBackPage extends MyAbstractWebPage{
	public CallBackPage(PageParameters parameters){
		super(parameters);
		
		String request_token = ((AuthSession)getSession()).getRequest_token();
		String request_token_secret = ((AuthSession)getSession()).getRequest_token_secret();
//		String oauth_token = parameters.getStringValue("oauth_token").toString();
		String oauth_verifier = parameters.getStringValue("oauth_verifier").toString();
		
		Twitter twitter = getTwitter();
		try {
			AccessToken accessToken = twitter.getOAuthAccessToken(request_token, request_token_secret, oauth_verifier);
			twitter.setOAuthAccessToken(accessToken);
			slimservice.setAccessToken(getUser(), accessToken.getToken(), accessToken.getTokenSecret(), twitter.verifyCredentials().getScreenName());
			setRedirect(true);
			throw new RestartResponseException(AuthPage.class);
		} catch (TwitterException e) {
			error("Twitter API Error...");
			throw new RestartResponseException(AuthPage.class);
//			e.printStackTrace();
		}
	}

	@Override
	protected String getContentTitle() {
		return "みんなのボット - CallBack";
	}
}
