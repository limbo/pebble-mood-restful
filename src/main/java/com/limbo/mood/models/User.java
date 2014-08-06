package com.limbo.mood.models;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.jetty.server.handler.ContextHandler;

@XmlRootElement
public class User {
	private static final String collectionName = "mood-users";
	
	@JsonProperty("email")
	private String email;
	@JsonProperty("password")
	private String password;

	public User() {
		email = new String();
		password = new String();
	}
	
	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public static String getCollectionName() {
		String postfix = (String)ContextHandler.getCurrentContext().getAttribute("collection-postfix");
		if (postfix != null) {
			return collectionName.concat(postfix);
		} else {
			return collectionName;
		}
		
	}
}
