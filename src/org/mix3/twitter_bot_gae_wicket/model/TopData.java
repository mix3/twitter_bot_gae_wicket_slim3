package org.mix3.twitter_bot_gae_wicket.model;

import java.io.Serializable;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@Model
@SuppressWarnings("serial")
public class TopData implements Serializable{
	@Attribute(primaryKey=true)
	private Key key;
	private Text data;
	
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public Text getData() {
		return data;
	}
	public void setData(Text data) {
		this.data = data;
	}
}
