package com.limbo.mood;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import org.eclipse.jetty.server.handler.ContextHandler;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;

/**
 * Provides access to MongodbHQ service.
 * @author limbo
 *
 * TODO: better exception handling. Probably just log and fail.
 */
public class MongoHQHandler {
	private static final MongoClientURI mongoURI = initURI();
	private static final MongoClient mongoClient = initClient(mongoURI);
	private static final DB db = initDB(mongoClient, mongoURI.getDatabase());
	
	
	private static MongoClientURI initURI() {
		MongoClientURI uri = null;
		try {
			Properties props = System.getProperties();
			URL r = ContextHandler.getCurrentContext().getResource("/WEB-INF/mongohq.properties");
	   		props.load(r.openStream());
			uri = new MongoClientURI(props.getProperty("mongohq-url"));
	   		System.err.println("MongoURI: " + uri.toString());
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return uri;
	}
	
	private static DB initDB(MongoClient client, String dbName) {
		DB db_ = null;
		db_ = mongoClient.getDB(dbName);

		return db_;
	}
	private static MongoClient initClient(MongoClientURI uri) {
		MongoClient client = null;
		
		try {	   		
	   		client = new MongoClient(uri);
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return client;
	}
	
	private static DB getDB() {
		return db;
	}
	
	public static DBCollection getCollection(String name) {
		String configured = (String)ContextHandler.getCurrentContext().getAttribute("collection");
		if (configured != null) {
			System.err.println(configured);
			return getDB().getCollection(configured);
		} else {
			System.err.println(name);
			return getDB().getCollection(name);
		}
	}
}
