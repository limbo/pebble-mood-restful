package com.limbo.mood.models;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class User {
	public static final String collectionName = "mood-users";
	
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
}
