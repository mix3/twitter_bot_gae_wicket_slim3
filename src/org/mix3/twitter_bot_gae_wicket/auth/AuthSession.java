package org.mix3.twitter_bot_gae_wicket.auth;

import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AuthSession extends AuthenticatedWebSession{
	private String request_token;
	private String request_token_secret;
	
	public AuthSession(Request request) {
		super(request);
	}

	@Override
	public boolean authenticate(String userid, String password) {
		return UserServiceFactory.getUserService().isUserAdmin();
	}

	@Override
	public Roles getRoles() {
		User user = UserServiceFactory.getUserService().getCurrentUser();
		if(user == null){
			return new Roles(MetaDataRoleAuthorizationStrategy.NO_ROLE);
		}else if(UserServiceFactory.getUserService().isUserAdmin()){
			return new Roles(Roles.ADMIN+","+Roles.USER);
		}else{
			return new Roles(Roles.USER);
		}
	}
	
	public String getRequest_token() {
		return request_token;
	}
	public void setRequest_token(String request_token) {
		this.request_token = request_token;
	}
	public String getRequest_token_secret() {
		return request_token_secret;
	}
	public void setRequest_token_secret(String request_token_secret) {
		this.request_token_secret = request_token_secret;
	}
}
