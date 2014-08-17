package com.limbo.mood.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;

import com.limbo.mood.MongoHQHandler;
import com.limbo.mood.models.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

	@GET
	@Path("/{email}")
	public Response get(@PathParam("email") String email) {
		System.err.println("Get User");
		Response r;
		
		System.err.println("GET User: " + email);
		
		DBObject q = QueryBuilder.start("email").is(email).get();
		System.err.println("QUERY: " + q.toString());
		DBObject obj = MongoHQHandler.getCollection(User.getCollectionName()).findOne(q);
		if (obj != null) {
			obj.removeField("_id");
			r = Response.ok(obj).build();
		} else {
			r = Response.serverError().status(404).build();
		}
		
		return r;
		
	}
	
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@Context UriInfo uriInfo, User user) {
    	
    	DBObject newUser = new BasicDBObject();
    	newUser.put("email", user.getEmail());
    	newUser.put("password", cryptPassword(user.getPassword()));

    	Response r;
    	try {
    		String id = MongoHQHandler.insert(User.getCollectionName(), newUser, true);
    		
    		URI uri = new URI(uriInfo.getAbsolutePath().toString() + "/" + id);
        	r = Response.created(uri).build();
    	} catch (DuplicateKeyException dupe) {
    		System.err.println("Duplicate user key: " + dupe.getMessage());
    		r = Response.status(Status.CONFLICT).entity(user).build();
     	} catch (MongoException e) {
        	System.err.println("User MONGO EX: " + e.getMessage());
        	r = Response.serverError().status(500).build();
    	} catch (URISyntaxException e) {
        	System.err.println("User POST URI EX: " + e.getMessage());
        	r = Response.serverError().status(500).build();
		}
    	return r;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/auth")
    public Response authenticate(@Context Request request, User user) {
    	Response r;
    	String crypted = cryptPassword(user.getPassword());
    	
    	System.err.println("AUTH Email:" + user.getEmail());
    	System.err.println("AUTH Pass:" + user.getPassword());
    	
    	DBObject q = QueryBuilder.start("email").is(user.getEmail()).and("password").is(crypted).get();
		System.err.println("QUERY: " + q.toString());
		DBObject obj = MongoHQHandler.getCollection(User.getCollectionName()).findOne(q);
		
		if (obj != null) {
			String token = userToken(obj.get("email").toString());
			obj.put("authtoken", token);
			MongoHQHandler.getCollection(User.getCollectionName()).save(obj);
			r = Response.ok("{\"authtoken\": \"" + token + "\"}").build();
		} else {
			r = Response.serverError().status(403).build();
		}
    	return r;
    }
    
    
    // -- Utility functions
    private String cryptPassword(String plain) {
	    
	    
	    MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
	        md.update(plain.getBytes());

	        return Base64.encodeBase64URLSafeString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
    
    private String userToken(String email) {
	    MessageDigest md;
	    String text = email + Long.toString((new Date()).getTime());
	    
		try {
			md = MessageDigest.getInstance("MD5");
	        md.update(text.getBytes());

	        return Base64.encodeBase64URLSafeString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			return null;
		}
    	
    }

}
