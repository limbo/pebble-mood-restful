package com.limbo.mood.services;

import static com.eclipsesource.restfuse.Assert.assertOk;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

import com.eclipsesource.restfuse.Destination;
import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.MediaType;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.RequestContext;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;
import com.limbo.mood.MongoHQHandlerTest;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@RunWith(HttpJUnitRunner.class)
public class RatingServiceTest {
    
	private static final String testCollectionName = "mood-rating-test";
	private static String testID = "moo";
	
	private static void startService() throws Exception {
        String webappDirLocation = "src/main/webapp/";
        
        
        Server server = new Server(8080);
        WebAppContext context = new WebAppContext();

        context.setContextPath("/");
        context.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        System.err.println("DESC: " + context.getDescriptor());
        context.setResourceBase(webappDirLocation);
        System.err.println("BASE: " + context.getResourceBase());
        context.setParentLoaderPriority(true);
//        root.setClassLoader(this.getClass().getClassLoader());
        context.setAttribute("collection", testCollectionName);
        
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
	
	@BeforeClass
	public static void setup() {
		// start web service
		try {
			startService();
		} catch (Exception e) {
			Assert.assertNull("failed to start server: " + e.getMessage(), e);
		}
		
        // drop collection
		DB db = MongoHQHandlerTest.getDB();
		if (db.getCollection(testCollectionName) != null) {
			db.getCollection(testCollectionName).drop();
		}
		
		db.createCollection(testCollectionName, null);
		
    	testID = createTestRating();
    	assertNotNull(testID);

	}
	
	private static String createTestRating() {
    	DBCollection testCollection = MongoHQHandlerTest.getCollection(testCollectionName);

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

    	WriteResult result = testCollection.insert(newRating);
    	return newRating.get("_id").toString();
	}
	
	@Rule
	public Destination destination = getDestination();
	
	private Destination getDestination() {
//			setup();

//			testID = "53da75b25a15815fbc000001";
		System.err.println("DEST");
	    Destination destination = new Destination( this,"http://127.0.0.1:8080/");
	    RequestContext context = destination.getRequestContext();
	    context.addPathSegment("id", testID);
	    return destination;
	}
	
	@Context
	private Response response; // will be injected after every request

	@HttpTest( method = Method.GET, path = "/services/rating/{id}" )
    public void testGet() throws Exception {
    	
    	assertOk(response);
    	
    }
	
    @HttpTest(method=Method.GET, path = "/services/rating/report/day")
    public void testReportDay() throws Exception {
    	assertOk(response);
    }

    @HttpTest(method=Method.GET, path = "/services/rating/report/week")
    public void testReportWeek() throws Exception {
    	assertOk(response);
    }

    @HttpTest(method=Method.GET, path = "/services/rating/report/month")
    public void testReportMonth() throws Exception {
    	assertOk(response);
    }
    
    /**
     * TODO: add closed cycle test POST -> GET
     * @throws Exception
     */
    @HttpTest(method=Method.POST, path = "/services/rating", type = MediaType.APPLICATION_JSON, content = "{\"rating\" : 6,  \"longitude\" : -122.2774162889666,  \"lat\" : 37.81409106220846,  \"locations\" : \"home\",  \"tags\" : [\"test1\", \"test2\"], \"people\" : [\"tester1\", \"tester2\"]}")
    public void testPost() throws Exception {
        assertEquals(201, response.getStatus());
    }

}
