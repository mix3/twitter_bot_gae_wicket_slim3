package org.mix3.twitter_bot_gae_wicket.model;

import java.io.Serializable;
import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

@Model
@SuppressWarnings("serial")
public class Message implements Serializable{
	@Attribute(primaryKey=true)
	private Key key;
	private boolean type;
	private String keyword;
	private String message;
	private Date date;
	
	public void clear(){
		this.key = null;
		this.type = false;
		this.keyword = null;
		this.message = null;
		this.date = null;
	}
	
	// getter/setter
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public boolean isType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
