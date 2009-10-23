package org.mix3.twitter_bot_gae_wicket;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.HttpSessionStore;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.coding.SharedResourceRequestTargetUrlCodingStrategy;
import org.apache.wicket.session.ISessionStore;
import org.mix3.twitter_bot_gae_wicket.auth.AuthSession;
import org.mix3.twitter_bot_gae_wicket.auth.Redirect;
import org.mix3.twitter_bot_gae_wicket.auth.SignInPage;
import org.mix3.twitter_bot_gae_wicket.page.EditPage;
import org.mix3.twitter_bot_gae_wicket.page.IndexPage;
import org.mix3.twitter_bot_gae_wicket.page.ListPage;
import org.mix3.twitter_bot_gae_wicket.page.admin.AdminPage;
import org.mix3.twitter_bot_gae_wicket.page.cron.Direct;
import org.mix3.twitter_bot_gae_wicket.page.cron.DirectQueue;
import org.mix3.twitter_bot_gae_wicket.page.cron.Post;
import org.mix3.twitter_bot_gae_wicket.page.cron.Replies;
import org.mix3.twitter_bot_gae_wicket.page.cron.RepliesQueue;
import org.mix3.twitter_bot_gae_wicket.page.cron.Token;
import org.mix3.twitter_bot_gae_wicket.page.cron.TokenQueue;
import org.mix3.twitter_bot_gae_wicket.page.twitter.AuthPage;
import org.mix3.twitter_bot_gae_wicket.page.twitter.CallBackPage;
import org.mix3.twitter_bot_gae_wicket.utils.MyModificationWatcher;
import org.odlabs.wiquery.core.commons.WiQueryInstantiationListener;

public class WicketApplication extends AuthenticatedWebApplication{
	// wickext
	private WiQueryInstantiationListener wiqueryPluginInstantiationListener;
	
	private boolean isLocalMode = true;
	
	public WicketApplication(){
		System.out.println("WicketApplication constructor()");
	}
	
	@Override
	protected ISessionStore newSessionStore() {
		return new HttpSessionStore(this);
	}
	
	@Override
	public String getConfigurationType() {
		isLocalMode = super.getServletContext().getServerInfo().startsWith("Google App Engine Development");
	    return isLocalMode ? Application.DEVELOPMENT : Application.DEPLOYMENT;
	}
	@Override
	protected WebRequest newWebRequest( HttpServletRequest servletRequest ){
	    if (isLocalMode) {
	        getResourceSettings().getResourceWatcher(true).start(
	                getResourceSettings().getResourcePollFrequency());
	    }
	    return super.newWebRequest(servletRequest);
	}
	
	@Override  
	protected void init() {
		// wickext
		wiqueryPluginInstantiationListener = new WiQueryInstantiationListener();
		addComponentInstantiationListener(wiqueryPluginInstantiationListener);
		
		super.init();
	    if (isLocalMode) {
	        getResourceSettings().setResourceWatcher(new MyModificationWatcher());
	    }
		
		addComponentInstantiationListener(new GuiceComponentInjector(this));
		
		getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
		getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
		
		mountBookmarkablePage("/admin", AdminPage.class);
		mountBookmarkablePage("/redirect", Redirect.class);
		mountBookmarkablePage("/list", ListPage.class);
		mountBookmarkablePage("/edit", EditPage.class);
		
		mountBookmarkablePage("/post", Post.class);
		mountBookmarkablePage("/replies", Replies.class);
		mountBookmarkablePage("/replies_queue", RepliesQueue.class);
		mountBookmarkablePage("/direct", Direct.class);
		mountBookmarkablePage("/direct_queue", DirectQueue.class);
		mountBookmarkablePage("/token", Token.class);
		mountBookmarkablePage("/token_queue", TokenQueue.class);
		
		mountBookmarkablePage("/twitter_authorize", AuthPage.class);
		mountBookmarkablePage("/twitter_callback", CallBackPage.class);
		
		ResourceReference favicon = new ResourceReference(WicketApplication.class, "favicon.ico");
		mount(new SharedResourceRequestTargetUrlCodingStrategy("/favicon.ico", favicon.getSharedResourceKey()));
	}
	
	@Override
	public Class<? extends WebPage> getHomePage() {
		return IndexPage.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return AuthSession.class;
	}
}
