package org.mix3.twitter_bot_gae_wicket.model;

import java.io.Serializable;
import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

@Model
@SuppressWarnings("serial")
public class SinceID implements Serializable{
	@Attribute(primaryKey=true)
	private Key key;
	private Long repliesSinceID;
	private Date createdAt;
	
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public Long getRepliesSinceID() {
		return repliesSinceID;
	}
	public void setRepliesSinceID(Long repliesSinceID) {
		this.repliesSinceID = repliesSinceID;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
