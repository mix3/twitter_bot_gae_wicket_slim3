package org.mix3.twitter_bot_gae_wicket.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.mix3.twitter_bot_gae_wicket.model.Message;
import org.mix3.twitter_bot_gae_wicket.model.TopData;
import org.mix3.twitter_bot_gae_wicket.model.User;

import twitter4j.TwitterException;
import twitter4j.http.AccessToken;

import com.google.inject.ImplementedBy;

@ImplementedBy(SlimServiceImpl.class)
public interface SlimService{
	public User getUser(String userid);
	public void register(String userid);
	
	public List<Map<String, Object>> getAll(int offset, int limit);
	public List<Message> get(int offset, int limit);
	public List<Message> get(String userid, int offset, int limit);
	public int count();
	public int count(String userid);
	public void put(String userid, Message message);
	public void multiInput(String userid, String data) throws IOException;
	public void update(Message message);
	
	public void post() throws TwitterException;
	public void replies() throws TwitterException;
	public void repliesQueue(String username, String message, String statusid) throws TwitterException;
	public void direct() throws TwitterException;
	public void directQueue(String username, String message) throws TwitterException;
	public void token();
	public void tokenQueue(String userid, String access_token, String access_token_secret) throws TwitterException;
	
	public void setAccessToken(String userid, String access_token, String access_token_secret, String screenName) throws TwitterException;
	public AccessToken getAccessToken(String userid);
	public void delAccessToken(String userid);
	
	public TopData getTopData();
	public void updateTopData(TopData td);
}
