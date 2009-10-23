package org.mix3.twitter_bot_gae_wicket.page.admin;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.mix3.twitter_bot_gae_wicket.page.MyAbstractWebPage;

public class PreviewPage extends MyAbstractWebPage{
	public PreviewPage(PageParameters parameters){
		super(parameters);
		String data = parameters.getString("data");
		Label label = new Label("data", data);
		label.setEscapeModelStrings(false);
		add(label);
	}
	@Override
	protected String getContentTitle() {
		return "みんなのボット - Preview";
	}

}
