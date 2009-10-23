package org.mix3.twitter_bot_gae_wicket.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.mix3.twitter_bot_gae_wicket.WicketApplication;
import org.mix3.twitter_bot_gae_wicket.page.admin.AdminPage;
import org.mix3.twitter_bot_gae_wicket.page.twitter.AuthPage;
import org.mix3.twitter_bot_gae_wicket.service.SlimService;
import org.mix3.twitter_bot_gae_wicket.utils.Utils;

import twitter4j.Twitter;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;

public abstract class MyAbstractWebPage extends WebPage{
	@Inject
	protected SlimService slimservice;
	
	@SuppressWarnings("serial")
	public MyAbstractWebPage(PageParameters parameters){
		add(CSSPackageResource.getHeaderContribution(WicketApplication.class, "resources/style.css"));
		add(new Label("header_title", getContentTitle()));
		add(new BookmarkablePageLink<WebPage>("contents_title", this.getClass()){
			@Override
			protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
				replaceComponentTagBody(markupStream, openTag, getContentTitle());
			}
			
		});
		
		BookmarkablePageLink<WebPage> top = new BookmarkablePageLink<WebPage>("top", getApplication().getHomePage());
		add(top);
		BookmarkablePageLink<WebPage> list = new BookmarkablePageLink<WebPage>("list", ListPage.class);
		MetaDataRoleAuthorizationStrategy.authorize(list, RENDER, Roles.USER);
		add(list);
		BookmarkablePageLink<WebPage> edit = new BookmarkablePageLink<WebPage>("edit", EditPage.class);
		MetaDataRoleAuthorizationStrategy.authorize(edit, RENDER, Roles.USER);
		add(edit);
		BookmarkablePageLink<WebPage> oauth = new BookmarkablePageLink<WebPage>("oauth", AuthPage.class);
		MetaDataRoleAuthorizationStrategy.authorize(oauth, RENDER, Roles.USER);
		add(oauth);
		BookmarkablePageLink<WebPage> admin = new BookmarkablePageLink<WebPage>("admin", AdminPage.class);
		MetaDataRoleAuthorizationStrategy.authorize(admin, RENDER, Roles.ADMIN);
		add(admin);
		
		UserService userService = UserServiceFactory.getUserService();
		ExternalLink login = new ExternalLink("login", userService.createLoginURL("/redirect"));
		MetaDataRoleAuthorizationStrategy.authorize(login, RENDER, MetaDataRoleAuthorizationStrategy.NO_ROLE);
		add(login);
		ExternalLink logout = new ExternalLink("logout", userService.createLogoutURL("/"));
		MetaDataRoleAuthorizationStrategy.authorize(logout, RENDER, Roles.USER);
		add(logout);
	}
	
	
	private String getProperty(String key) {
		return Utils.getTwitterProperties().getProperty(key);
	}
	
	protected Twitter getTwitter(){
		Twitter twitter = new Twitter();
		twitter.setOAuthConsumer(
				getProperty("consumer.key"),
				getProperty("consumer.secret")
		);
		return twitter;
	}
	
	protected String getUser(){
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser().getUserId();
	}
	
	abstract protected String getContentTitle();
}
