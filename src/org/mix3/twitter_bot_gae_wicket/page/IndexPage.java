package org.mix3.twitter_bot_gae_wicket.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

public class IndexPage extends MyAbstractWebPage{
	public IndexPage(PageParameters parameters){
		super(parameters);
		Label label = new Label("info", slimservice.getTopData().getData().getValue());
		label.setEscapeModelStrings(false);
		add(label);
	}
	
	@Override
	protected String getContentTitle() {
		return "みんなのボット";
	}
}
