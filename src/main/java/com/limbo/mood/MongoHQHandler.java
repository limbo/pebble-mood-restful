package com.limbo.mood;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.MongoURI;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

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
	
	public static String insert(String collection, DBObject o, boolean withID) {
    	try {
    		o.put("_id", new ObjectId());
    		WriteResult result = getCollection(collection).insert(o, WriteConcern.ACKNOWLEDGED);
    		
        	if (withID) {
        		return o.get("_id").toString();
        	} else {
        		return null;
        	}
     	} catch (MongoException e) {
        	System.err.println("RATING MONGO EX: " + e.getMessage());
        	return null;
    	}
		
	}
	
	public static DBObject findById(String collectionName, String id) {
		DBObject q = QueryBuilder.start("_id").is(new ObjectId(id)).get();
		System.err.println("QUERY: " + q.toString());
		
		return getCollection(collectionName).findOne(q);
	}
}
