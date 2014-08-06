package com.limbo.mood.services;

import java.util.Date;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;

import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.limbo.mood.MongoHQHandlerTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public abstract class AbstractServiceTest {

	public static final String testCollectionPostfix = "-test";
	public static final String ratingCollectionName = "mood-ratings-test";
	public static final String userCollectionName = "mood-users-test";
	
	protected static String testID = "moo";
	protected static String testAuthToken = "testAuthTokenValue";
	
	private static Server server;
	
	private static void startService() throws Exception {
        String webappDirLocation = "src/main/webapp/";
        
        
        server = new Server(8080);
        WebAppContext context = new WebAppContext();

        context.setContextPath("/");
        context.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        System.err.println("DESC: " + context.getDescriptor());
        context.setResourceBase(webappDirLocation);
        System.err.println("BASE: " + context.getResourceBase());
        context.setParentLoaderPriority(true);
//        root.setClassLoader(this.getClass().getClassLoader());
        context.setAttribute("collection-postfix", testCollectionPostfix);
        
        server.setHandler(context);

        server.start();
        server.setStopAtShutdown(true);
        long start = System.currentTimeMillis();
        while (!server.isStarted() && System.currentTimeMillis() < (start + 5 * 1000)) {
            //
        }
        if (!server.isStarted()) {
            throw new Exception("Jetty didn't start in time");
        }	
        System.err.println("Started!");
    }
	
	public static void setup() {
		// start web service
		try {
			startService();
		} catch (Exception e) {
			Assert.assertNull("failed to start server: " + e.getMessage(), e);
		}
		
        // drop collection
		DB db = MongoHQHandlerTest.getDB();
		if (db.getCollection(ratingCollectionName) != null) {
			db.getCollection(ratingCollectionName).drop();
		}
		if (db.getCollection(userCollectionName) != null) {
			db.getCollection(userCollectionName).drop();
		}
		
		db.createCollection(ratingCollectionName, null);
		db.createCollection(userCollectionName, null);
		
		setupTestData();
	}

	/**
	 * Override me!
	 */
	public static void setupTestData() {
		
	}
	
	private static void stopServer() throws Exception {
		server.stop();
		server.join();
	}
	
	@AfterClass
	public static void cleanup() throws Exception {
		stopServer();
	}
	
	protected static String createTestRating() {
    	DBCollection testCollection = MongoHQHandlerTest.getCollection(ratingCollectionName);

    	DBObject newRating = new BasicDBObject();
    	newRating.put("time", new Date());
    	newRating.put("rating", 5);
    	newRating.put("locations", "test");
    	newRating.put("tags1", "test1");
    	newRating.put("tags2", "test2");
    	newRating.put("tags3", "test3");
    	newRating.put("tags4", null);
    	newRating.put("person1", "tester1");
    	newRating.put("person2", null);
    	newRating.put("person3", "tester2");
    	newRating.put("person4", "tester3");

    	testCollection.insert(newRating);
    	return newRating.get("_id").toString();
	}
	
	protected static String createTestUser() {
    	DBCollection testCollection = MongoHQHandlerTest.getCollection(userCollectionName);

    	DBObject newUser = new BasicDBObject();
    	newUser.put("email", "tester@test.com");
    	newUser.put("password", "NieQminDE4Ggcewn98nKl3Jhgq7Smn3dLlQ1MyLPswq7njpt8qwsIP4jQ2MR1nhWTQyNMFkwV19g4tPQSBhNeQ");

    	WriteResult result = testCollection.insert(newUser);
    	return newUser.get("_id").toString();
	}

	protected static void createTestUserWithAuth() {
    	DBCollection testCollection = MongoHQHandlerTest.getCollection(userCollectionName);

    	DBObject newUser = new BasicDBObject();
    	newUser.put("email", "tester@test.com");
    	newUser.put("password", "NieQminDE4Ggcewn98nKl3Jhgq7Smn3dLlQ1MyLPswq7njpt8qwsIP4jQ2MR1nhWTQyNMFkwV19g4tPQSBhNeQ");
    	newUser.put("authtoken", testAuthToken);

    	testCollection.insert(newUser);
	}

}