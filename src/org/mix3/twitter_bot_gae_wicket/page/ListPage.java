package org.mix3.twitter_bot_gae_wicket.page;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.mix3.twitter_bot_gae_wicket.model.Message;
import org.odlabs.wiquery.plugins.treeview.TreeViewPlugin;

@AuthorizeInstantiation({Roles.ADMIN, Roles.USER})
public class ListPage extends MyAbstractWebPage{
	@SuppressWarnings("serial")
	public ListPage(PageParameters parameters){
		super(parameters);
		
        final TreeViewPlugin tree = new TreeViewPlugin("tree");
        tree.setCollapsed(true);
        add(tree.setOutputMarkupId(true));
		
		IDataProvider<Map<String, Object>> provider = new IDataProvider<Map<String, Object>>(){
			@Override
			public Iterator<? extends Map<String, Object>> iterator(int first, int count) {
				return slimservice.getAll(first, count).iterator();
			}
			@SuppressWarnings("unchecked")
			@Override
			public IModel<Map<String, Object>> model(Map<String, Object> map) {
				return new Model((Serializable) map);
			}
			@Override
			public int size() {
				return slimservice.count();
			}
			@Override
			public void detach() {
			}
		};
		
		DataView<Map<String, Object>> view = new DataView<Map<String, Object>>("list", provider, 25){
			@Override
			protected void populateItem(Item<Map<String, Object>> item) {
				item.setDefaultModel(new CompoundPropertyModel<Map<String, Object>>(item.getModelObject()));
				item.add(new Label("user.screenName"));
				item.add(new ListView<Message>("messages"){
					@Override
					protected void populateItem(ListItem<Message> item) {
						item.setDefaultModel(new CompoundPropertyModel<Message>(item.getModelObject()));
						if(item.getModelObject().isType()){
							item.add(new Label("type", "Replies").add(new SimpleAttributeModifier("class", "type_replies")));
						}else{
							item.add(new Label("type", "Message").add(new SimpleAttributeModifier("class", "type_message")));
						}
						WebMarkupContainer keyword = new WebMarkupContainer("keyword_container");
						item.add(keyword.add(new Label("keyword")).setVisible(item.getModelObject().isType()));
						item.add(new Label("message"));
						item.add(new Label("date"));
					}
				});
			}
		};
		tree.add(view);
		tree.add(new AjaxPagingNavigator("paging", view));
	}
	
	@Override
	protected String getContentTitle() {
		return "みんなのボット - 一覧";
	}
}
