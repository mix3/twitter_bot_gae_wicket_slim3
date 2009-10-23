package org.mix3.twitter_bot_gae_wicket.page;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.mix3.twitter_bot_gae_wicket.model.Message;

@SuppressWarnings("serial")
@AuthorizeInstantiation({Roles.ADMIN, Roles.USER})
public class EditPage extends MyAbstractWebPage{
	private Message input = new Message();
	private String data = "";
	private boolean toggle = false;
	
	public EditPage(PageParameters parameters){
		super(parameters);
		
		final FeedbackPanel feedback = new FeedbackPanel("feedback");
        add(feedback.setOutputMarkupId(true));
        
		final WebMarkupContainer container = new WebMarkupContainer("container");
		add(container.setOutputMarkupId(true));
		
		IDataProvider<Message> provider = new IDataProvider<Message>(){
			@Override
			public Iterator<? extends Message> iterator(int first, int count) {
				return slimservice.get(getUser(), first, count).iterator();
			}
			@Override
			public IModel<Message> model(Message message) {
				return new Model<Message>(message);
			}
			@Override
			public int size() {
				return slimservice.count(getUser());
			}
			@Override
			public void detach() {
			}
		};
		DataView<Message> view = new DataView<Message>("list", provider, 25){
			@Override
			protected void populateItem(final Item<Message> item) {
				Message message = item.getModelObject();
				if(message.isType()){
					item.add(new Label("type", "Replies").add(new SimpleAttributeModifier("class", "type_replies")));
				}else{
					item.add(new Label("type", "Message").add(new SimpleAttributeModifier("class", "type_message")));
				}
				final WebMarkupContainer keyword = new WebMarkupContainer("keyword_container");
				keyword.setVisible(message.isType());
				keyword.add(new AjaxEditableLabel<String>("keyword", new PropertyModel<String>(message, "keyword")){
					@Override
					protected void onError(AjaxRequestTarget target) {
						super.onError(target);
						target.addComponent(container);
						target.addComponent(feedback);
					}

					@Override
					protected FormComponent<String> newEditor(MarkupContainer parent, String componentId, IModel<String> model) {
						super.newEditor(parent, componentId, model);
						FormComponent<String> c = super.newEditor(parent, componentId, model);
						if(model.getObject() != null){
							c.add(new SimpleAttributeModifier("size", "\""+model.getObject().length()*2+"\""));
						}
						return c;
					}

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						super.onSubmit(target);
						slimservice.update(item.getModelObject());
						target.addComponent(container);
						target.addComponent(feedback);
					}
				});
				item.add(keyword);
				item.add(new AjaxEditableLabel<String>("message", new PropertyModel<String>(message, "message")){
					@Override
					protected void onError(AjaxRequestTarget target) {
						super.onError(target);
						target.addComponent(container);
						target.addComponent(feedback);
					}

					@Override
					protected FormComponent<String> newEditor(MarkupContainer parent, String componentId, IModel<String> model) {
						super.newEditor(parent, componentId, model);
						FormComponent<String> c = super.newEditor(parent, componentId, model);
						c.add(new SimpleAttributeModifier("size", "\""+model.getObject().length()*2+"\""));
						return c;
					}

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						super.onSubmit(target);
						slimservice.update(item.getModelObject());
						target.addComponent(container);
						target.addComponent(feedback);
					}
				}.add(StringValidator.maximumLength(120)));
				item.add(new Label("date", new PropertyModel<Date>(message, "date")));
			}
		};
		container.add(view);
		container.add(new AjaxPagingNavigator("paging", view));
		
		final WebMarkupContainer form_container = new WebMarkupContainer("form_container");
		add(form_container.setOutputMarkupId(true));
		
		final Form<Message> form = new Form<Message>("message");
		form_container.add(form.setOutputMarkupId(true));
		
		final WebMarkupContainer keyword = new WebMarkupContainer("keyword_container");
		keyword.add(new RequiredTextField<String>("keyword", new PropertyModel<String>(input, "keyword")));
		keyword.setVisible(false);
		form.add(keyword);
		form.add(new AjaxCheckBox("type", new PropertyModel<Boolean>(input, "type")){
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				keyword.setVisible(getModelObject());
				target.addComponent(form);
			}
		});
		form.add(new RequiredTextField<String>("message", new PropertyModel<String>(input, "message")));
		form.add(new AjaxButton("submit", form){
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.addComponent(feedback);
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				input.setDate(new Date(System.currentTimeMillis()));
				slimservice.put(getUser(), input);
				input.clear();
				keyword.setVisible(false);
				target.addComponent(container);
				target.addComponent(form);
				target.addComponent(feedback);
			}
		});
		
		final Form<String> multi = new Form<String>("multi");
		multi.setVisible(toggle);
		form_container.add(multi.setOutputMarkupId(true));
		
		TextArea<String> multiInput = new TextArea<String>("data", new PropertyModel<String>(this, "data"));
		multiInput.setRequired(true);
		multi.add(multiInput);
		multi.add(new AjaxButton("submit"){
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.addComponent(feedback);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					slimservice.multiInput(getUser(), data);
					info("OK!");
					data = "";
					target.addComponent(multi);
					target.addComponent(container);
					target.addComponent(feedback);
				} catch (IOException e) {
					error("IO Error...");
					target.addComponent(feedback);
				}
			}
		});
		add(new AjaxLink<Void>("switch"){
			@Override
			public void onClick(AjaxRequestTarget target) {
				toggle = !toggle;
				form.setVisible(!toggle);
				multi.setVisible(toggle);
				target.addComponent(form_container);
			}
		});
	}
	
	@Override
	protected String getContentTitle() {
		return "みんなのボット - 編集";
	}
}