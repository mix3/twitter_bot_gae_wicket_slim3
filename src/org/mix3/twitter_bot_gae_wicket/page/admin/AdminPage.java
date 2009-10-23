package org.mix3.twitter_bot_gae_wicket.page.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.mix3.twitter_bot_gae_wicket.model.Message;
import org.mix3.twitter_bot_gae_wicket.model.TopData;
import org.mix3.twitter_bot_gae_wicket.page.MyAbstractWebPage;

import com.google.appengine.api.datastore.Text;

import twitter4j.TwitterException;

@AuthorizeInstantiation(Roles.ADMIN)
public class AdminPage extends MyAbstractWebPage{
	private List<ITab> tabs = new ArrayList<ITab>();
	
	@SuppressWarnings("serial")
	public AdminPage(PageParameters parameters){
		super(parameters);
		
		add(CSSPackageResource.getHeaderContribution(AdminPage.class, "tab.css"));
		
		tabs.add(new AbstractTab(new Model<String>("Top Edit")){
			@Override
			public Panel getPanel(String id) {
				return new TopPanel(id);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Post Test")){
			@Override
			public Panel getPanel(String id) {
				return new PostPanel(id);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Replies Test")){
			@Override
			public Panel getPanel(String id) {
				return new RepliesPanel(id);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Direct Test")){
			@Override
			public Panel getPanel(String id) {
				return new DirectPanel(id);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Token Update")){
			@Override
			public Panel getPanel(String id) {
				return new TokenPanel(id);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Init")){
			@Override
			public Panel getPanel(String id) {
				return new InitPanel(id);
			}
		});
		
		add(new AjaxTabbedPanel("tabs", tabs));
	}
	
	@SuppressWarnings("serial")
	private class InitPanel extends Panel {
		private String data = "";
		
		public InitPanel(String id) {
			super(id);
			
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
	        add(feedback.setOutputMarkupId(true));
	        
			Form<Void> init = new Form<Void>("init");
			init.add(new TextArea<String>("data", new PropertyModel<String>(this, "data")));
			init.add(new AjaxButton("submit"){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					try{
						StringReader string_reader = new StringReader(data);
						BufferedReader reader = new BufferedReader(string_reader);
						String buffer;
						while((buffer = reader.readLine()) != null){
							Message m = new Message();
							m.setMessage(buffer);
							m.setType(false);
							m.setDate(new Date(System.currentTimeMillis()));
							slimservice.put(getUser(), m);
						}
						data = "";
						info("OK!");
					}catch(IOException e){
						error("IO Error...");
					}
					target.addComponent(feedback);
				}
			});
			add(init);
		}
	}
	
	@SuppressWarnings("serial")
	private class PostPanel extends Panel {
		public PostPanel(String id) {
			super(id);
			
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
	        add(feedback.setOutputMarkupId(true));
	        
	        Form<Void> post = new Form<Void>("post");
	        post.add(new AjaxButton("submit"){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					try {
						slimservice.post();
						info("OK!");
					} catch (TwitterException e) {
						error("Twitter API Error...");
					}
					target.addComponent(feedback);
				}
			});
	        add(post);
		}
	}
	
	@SuppressWarnings("serial")
	private class RepliesPanel extends Panel {
		public RepliesPanel(String id) {
			super(id);
			
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
	        add(feedback.setOutputMarkupId(true));
	        
	        Form<Void> post = new Form<Void>("replies");
	       	post.add(new AjaxButton("submit"){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					try {
						slimservice.replies();
						info("OK!");
					} catch (TwitterException e) {
						error("Twitter API Error...");
					}
					target.addComponent(feedback);
				}
			});
	        add(post);
		}
	}
	
	@SuppressWarnings("serial")
	private class DirectPanel extends Panel {
		public DirectPanel(String id) {
			super(id);
			
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
	        add(feedback.setOutputMarkupId(true));
	        
	        Form<Void> post = new Form<Void>("direct");
	        post.add(new AjaxButton("submit"){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					try {
						slimservice.direct();
						info("OK!");
					} catch (TwitterException e) {
						error("Twitter API Error...");
					}
					target.addComponent(feedback);
				}
			});
	        add(post);
		}
	}
	
	@SuppressWarnings("serial")
	private class TokenPanel extends Panel {
		public TokenPanel(String id) {
			super(id);
			
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
	        add(feedback.setOutputMarkupId(true));
	        
	        Form<Void> token = new Form<Void>("direct");
	        token.add(new AjaxButton("submit"){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					slimservice.token();
					info("OK!");
					target.addComponent(feedback);
				}
			});
	        add(token);
		}
	}
	
	@SuppressWarnings("serial")
	private class TopPanel extends Panel {
		private TopData data = slimservice.getTopData();
		
		public TopPanel(String id) {
			super(id);
			
			final ModalWindow window = new ModalWindow("preview");
			window.setInitialWidth(800);
			add(window);
			
			final FeedbackPanel feedback = new FeedbackPanel("feedback");
	        add(feedback.setOutputMarkupId(true));
	        
			final Form<TopData> init = new Form<TopData>("top");
			IModel<String> textModel = new IModel<String>(){
				@Override
				public String getObject() {
					return data.getData().getValue();
				}
				@Override
				public void setObject(String text) {
					data.setData(new Text(text));
				}
				@Override
				public void detach() {
				}
			};
			init.add(new TextArea<String>("data", textModel));
			init.add(new AjaxButton("submit"){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					System.out.println(data.getData());
					slimservice.updateTopData(data);
					info("OK!");
					target.addComponent(feedback);
				}
			});

			init.add(new AjaxButton("preview"){
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					window.setPageCreator(new ModalWindow.PageCreator(){
						@Override
						public Page createPage() {
							PageParameters param = new PageParameters();
							param.add("data", data.getData().getValue());
							return new PreviewPage(param);
						}
					});
					window.show(target);
				}
			});
			add(init);
		}
	}
	
	@Override
	protected String getContentTitle() {
		return "みんなのボット - Admin";
	}
}
