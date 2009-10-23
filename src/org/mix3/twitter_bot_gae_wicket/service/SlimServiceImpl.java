package org.mix3.twitter_bot_gae_wicket.service;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mix3.twitter_bot_gae_wicket.meta.MessageMeta;
import org.mix3.twitter_bot_gae_wicket.meta.SinceIDMeta;
import org.mix3.twitter_bot_gae_wicket.meta.TopDataMeta;
import org.mix3.twitter_bot_gae_wicket.meta.UserMeta;
import org.mix3.twitter_bot_gae_wicket.model.Message;
import org.mix3.twitter_bot_gae_wicket.model.SinceID;
import org.mix3.twitter_bot_gae_wicket.model.TopData;
import org.mix3.twitter_bot_gae_wicket.model.User;
import org.mix3.twitter_bot_gae_wicket.utils.Utils;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.DescCriterion;
import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.ModelMeta;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.inject.Singleton;

@Singleton
public class SlimServiceImpl implements SlimService{
	private static Logger logger = Logger.getLogger(SlimServiceImpl.class.getName());
	private Twitter twitter;
	
	public SlimServiceImpl(){
		twitter = new Twitter(
				getProperty("twitter.id"),
				getProperty("twitter.pass")
		);
	}
	
	private String getProperty(String key) {
		return Utils.getTwitterProperties().getProperty(key);
	}
	
	@Override
	public User getUser(String userid) {
		UserMeta um = new UserMeta();
		return Datastore.query(um).filter(um.userid.equal(userid)).asSingle();
	}
	
	@Override
	public void register(String userid) {
		User user = getUser(userid);
		if(user == null){
			Datastore.put(new User(userid));
		}
	}
	
	@Override
	public int count() {
		UserMeta um = new UserMeta();
		return Datastore.query(um).filter(um.screenName.isNotNull()).count();
	}
	
	@Override
	public int count(String userid) {
		return Datastore.query(new MessageMeta(), getUser(userid).getKey()).count();
	}
	
	@Override
	public List<Message> get(int offset, int limit) {
		if(limit == 0){
			limit = 1;
		}
		MessageMeta mm = new MessageMeta();
		return Datastore.query(mm).sort(new DescCriterion(mm.date)).offset(offset).limit(limit).asList();
	}
	
	@Override
	public List<Message> get(String userid, int offset, int limit) {
		if(limit == 0){
			limit = 1;
		}
		MessageMeta mm = new MessageMeta();
		return Datastore.query(mm, getUser(userid).getKey()).sort(new DescCriterion(mm.date)).offset(offset).limit(limit).asList();
	}
	
	@Override
	public AccessToken getAccessToken(String userid) {
		return getUser(userid).getAccessToken();
	}
	
	@Override
	public void setAccessToken(String userid, String access_token, String access_token_secret, String screenName) throws TwitterException {
		User user = getUser(userid);
		user.setAccess_token(access_token);
		user.setAccess_token_secret(access_token_secret);
		user.setScreenName(screenName);
		Datastore.put(user);
		twitter.createFriendship(screenName);
	}
	
	@Override
	public void put(String userid, Message message) {
		Key childKey = Datastore.allocateId(getUser(userid).getKey(),  new MessageMeta());
		message.setKey(childKey);
		Datastore.put(message);
	}
	
	@Override
	public void update(Message message) {
		if(message.isType() && (message.getMessage() == null || message.getKeyword() == null)){
			Datastore.delete(message.getKey());
		}else if(!message.isType() && message.getMessage() == null){
			Datastore.delete(message.getKey());
		}else{
			Datastore.put(message);
		}
	}
	
	@Override
	public void post() throws TwitterException {
		UserMeta um = new UserMeta();
		User user = getRandomEntity(um, um.access_token.isNotNull(), um.access_token.isNotNull());
		if(user == null){
			return;
		}
		
		MessageMeta mm = new MessageMeta();
		Message message = getRandomEntity(mm, user.getKey(), mm.type.equal(false));
		if(message == null){
			return;
		}
		
		Twitter oauth = new Twitter();
		oauth.setOAuthConsumer(
				getProperty("consumer.key"),
				getProperty("consumer.secret")
		);
		oauth.setOAuthAccessToken(user.getAccessToken());
		
		String name;
		try {
			name = oauth.verifyCredentials().getScreenName();
		} catch (TwitterException e) {
			return;
		}
		
		
		String update = message.getMessage()+" (via @"+name+")";
		logger.info(update);
		twitter.setRequestHeader("X-Twitter-Client", "みんなのボット");
		twitter.updateStatus(update);
	}
	
	private <T> T getRandomEntity(ModelMeta<T> mm, FilterCriterion... filterCriterion){
		int range = Datastore.query(mm).filter(filterCriterion).count();
		if(range > 0){
			Random r = new Random();
			int offset = r.nextInt(range);
			return (T)Datastore.query(mm).filter(filterCriterion).offset(offset).limit(1).asList().get(0);
		}
		return null;
	}
	
	private <T> T getRandomEntity(ModelMeta<T> mm, Key key, FilterCriterion... filterCriterion){
		int range = Datastore.query(mm, key).filter(filterCriterion).count();
		if(range > 0){
			Random r = new Random();
			int offset = r.nextInt(range);
			return (T)Datastore.query(mm, key).filter(filterCriterion).offset(offset).limit(1).asList().get(0);
		}
		return null;
	}
	
	@Override
	public void replies() throws TwitterException {
		List<Status> list = twitter.getMentions();
		if(list.isEmpty()){
			System.out.println("Replies emplty");
			return;
		}
		
		SinceIDMeta sid = new SinceIDMeta();
		SinceID r = Datastore.query(sid, getSingleKey(sid)).asSingle();
		if(r == null){
			r = new SinceID();
			r.setKey(getSingleKey(sid));
			r.setRepliesSinceID(4946784252L);
		}else{
			if(r.getRepliesSinceID() == null){
				r.setRepliesSinceID(4946784252L);
			}
		}
		
		for(Status s : list){
			if(r.getRepliesSinceID() < s.getId()){
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(
						url("/replies_queue")
						.param("message", s.getText())
						.param("username", s.getUser().getScreenName())
						.param("statusid", String.valueOf(s.getInReplyToStatusId()))
				);
			}
		}
		
		r.setRepliesSinceID(list.get(0).getId());
		Datastore.put(r);
	}

	@Override
	public void repliesQueue(String username, String message, String statusid) throws TwitterException {
		MessageMeta mm = new MessageMeta();
		List<Message> messageList = Datastore.query(mm).filter(mm.type.isNotNull(), mm.type.equal(true)).asList();
		List<Message> postList = new ArrayList<Message>();
		for(Message m : messageList){
			Pattern pattern = Pattern.compile("^(.*?)("+m.getKeyword()+")(.*?)$");
			Matcher matcher = pattern.matcher(message);
			if(matcher.matches()){
				postList.add(m);
			}
		}
		if(postList.isEmpty()){
			System.out.println("empty");
			return;
		}
		Random r = new Random();
		int offset = r.nextInt(postList.size());
		String update = "@"+username+" "+postList.get(offset).getMessage();
		logger.info(update);
		twitter.setRequestHeader("X-Twitter-Client", "みんなのボット");
		twitter.updateStatus(update, Long.valueOf(statusid));
	}

	@Override
	public void direct() throws TwitterException {
		List<DirectMessage> list = twitter.getDirectMessages();
		if(list.isEmpty()){
			System.out.println("DirectMessage emplty");
			return;
		}
		
		SinceIDMeta sim = new SinceIDMeta();
		SinceID r = Datastore.query(sim, getSingleKey(sim)).asSingle();
		if(r == null){
			r = new SinceID();
			r.setKey(getSingleKey(sim));
			r.setCreatedAt(new Date(0));
		}else{
			if(r.getCreatedAt() == null){
				r.setCreatedAt(new Date(0));
			}
		}
		
		for(DirectMessage dm : list){
			if(dm.getCreatedAt().after(r.getCreatedAt())){
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(
						url("/direct_queue")
						.param("message", dm.getText())
						.param("username", dm.getSenderScreenName())
				);
			}
		}
		
		r.setCreatedAt(list.get(0).getCreatedAt());
		Datastore.put(r);
	}
	
	@Override
	public void delAccessToken(String userid) {
		User user = getUser(userid);
		user.setAccess_token(null);
		user.setAccess_token_secret(null);
		user.setScreenName(null);
		Datastore.put(user);
	}

	@Override
	public TopData getTopData() {
		TopDataMeta tdm = new TopDataMeta();
		TopData td = Datastore.query(tdm, getSingleKey(tdm)).asSingle();
		if(td == null){
			td = new TopData();
			td.setKey(getSingleKey(tdm));
			td.setData(new Text(""));
			return td;
		}
		return td;
	}

	@Override
	public void updateTopData(TopData td) {
		Datastore.put(td);
	}
	
	private Key getSingleKey(ModelMeta<?> m){
		return Datastore.createKey(m, 1);
	}

	@Override
	public List<Map<String, Object>> getAll(int offset, int limit) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		UserMeta um = new UserMeta();
		List<User> users = Datastore.query(um).filter(um.screenName.isNotNull()).offset(offset).limit(limit).asList();
		MessageMeta mm = new MessageMeta();
		for(User user : users){
			Map<String, Object> map = new HashMap<String, Object>();
			List<Message> messages = Datastore.query(mm, user.getKey()).sort(mm.date.desc).asList();
			map.put("user", user);
			map.put("messages", messages);
			result.add(map);
		}
		return result;
	}

	@Override
	public void token() {
		UserMeta um = new UserMeta();
		List<User> users = Datastore.query(um).filter(um.access_token.isNotNull()).asList();
		for(User user : users){
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(
					url("/token_queue")
					.param("userid", user.getUserid())
					.param("access_token", user.getAccess_token())
					.param("access_token_secret", user.getAccess_token_secret())
			);
		}
	}

	@Override
	public void tokenQueue(String userid, String access_token, String access_token_secret) throws TwitterException {
		Twitter oauth = new Twitter();
		oauth.setOAuthConsumer(
				getProperty("consumer.key"),
				getProperty("consumer.secret")
		);
		
		oauth.setOAuthAccessToken(access_token, access_token_secret);
		User user = getUser(userid);
		user.setScreenName(oauth.verifyCredentials().getScreenName());
		Datastore.put(user);
	}

	@Override
	public void directQueue(String username, String message) throws TwitterException {
		UserMeta um = new UserMeta();
		User user = Datastore.query(um).filter(um.screenName.equal(username)).asList().get(0);
		
		Pattern p = Pattern.compile("^\\{\\{(.+)\\}\\}\\{\\{(.+)\\}\\}$");
		Matcher m = p.matcher(message);
		Message mess = new Message();
		if(m.matches()){
			mess.setKeyword(m.group(1));
			mess.setMessage(m.group(2));
			mess.setType(true);
			mess.setDate(new Date(System.currentTimeMillis()));
		}else{
			mess.setMessage(message);
			mess.setType(false);
			mess.setDate(new Date(System.currentTimeMillis()));
		}
		put(user.getUserid(), mess);
	}

	@Override
	public void multiInput(String userid, String data) throws IOException {
		Pattern p = Pattern.compile("^\\{\\{(.+)\\}\\}\\{\\{(.+)\\}\\}$");
		
		StringReader string_reader = new StringReader(data);
		LineNumberReader reader = new LineNumberReader(string_reader);
		String buffer;
		while((buffer = reader.readLine()) != null){
			Matcher m = p.matcher(buffer);
			Message mess = new Message();			
			if(m.matches()){
				mess.setKeyword(m.group(1));
				mess.setMessage(m.group(2));
				mess.setType(true);
				mess.setDate(new Date(System.currentTimeMillis()));
			}else{
				mess.setMessage(buffer);
				mess.setType(false);
				mess.setDate(new Date(System.currentTimeMillis()));
			}
			put(userid, mess);
		}
	}
}