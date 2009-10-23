package org.mix3.twitter_bot_gae_wicket.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.mix3.twitter_bot_gae_wicket.WicketApplication;

public class Utils {
	public static Properties getTwitterProperties(){
		Properties back = new Properties();
		InputStream is = WicketApplication.class.getResourceAsStream("/twitter.properties");
		if (is == null) {
			throw new RuntimeException("Unable to locate twitter.properties");
		}
		try {
			back.load(is);
			is.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to load twitter.properties");
		}
		return back;
	}
}
