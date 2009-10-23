package org.mix3.twitter_bot_gae_wicket.model;

import java.io.Serializable;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import twitter4j.http.AccessToken;

import com.google.appengine.api.datastore.Key;

@Model
@SuppressWarnings("serial")
public class User implements Serializable{
	@Attribute(primaryKey=true)
	private Key key;
	private String userid;
	private String access_token;
	private String access_token_secret;
	private String screenName;
	
	public User(){}
	public User(String userid){
		this.userid = userid;
	}
	
	// getter/setter
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getAccess_token_secret() {
		return access_token_secret;
	}
	public void setAccess_token_secret(String access_token_secret) {
		this.access_token_secret = access_token_secret;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public AccessToken getAccessToken(){
		if(access_token == null || access_token_secret == null){
			return null;
		}
		return new AccessToken(access_token, access_token_secret);
	}
}
