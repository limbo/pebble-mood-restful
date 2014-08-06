package com.limbo.mood;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import org.eclipse.jetty.server.handler.ContextHandler;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;

/**
 * Provides access to MongodbHQ service.
 * @author limbo
 *
 */
public class MongoHQHandlerTest {

	private static final DB db = initDB();
	
	private static DB initDB() {
		DB db_ = null;
		try {
			Properties props = System.getProperties();
			URL r = new URL("file:/Users/limbo/git/heroku/pebble-mood-restful/src/main/webapp/WEB-INF/mongohq.properties");
	   		props.load(r.openStream());
			MongoURI mongoURI = new MongoURI(props.getProperty("mongohq-url"));
	   		System.err.println("MongoURI: " + mongoURI.toString());
	   		
			db_ = mongoURI.connectDB();
			db_.authenticate(mongoURI.getUsername(), mongoURI.getPassword());
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
		
		return db_;
	}
	
	public static DB getDB() {
		return db;
	}
	
	public static DBCollection getCollection(String name) {
		return getDB().getCollection(name);
	}
}
