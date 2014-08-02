package com.limbo.mood.services;

import static com.eclipsesource.restfuse.Assert.assertOk;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
public class UserServiceTest {
    
	private static final String testCollectionName = "mood-user-test";
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
		
    	testID = createTestUser();
    	assertNotNull(testID);

	}
	
	private static String createTestUser() {
    	DBCollection testCollection = MongoHQHandlerTest.getCollection(testCollectionName);

    	DBObject newUser = new BasicDBObject();
    	newUser.put("email", "tester@test.com");
    	newUser.put("password", "NieQminDE4Ggcewn98nKl3Jhgq7Smn3dLlQ1MyLPswq7njpt8qwsIP4jQ2MR1nhWTQyNMFkwV19g4tPQSBhNeQ==");

    	WriteResult result = testCollection.insert(newUser);
    	return newUser.get("_id").toString();
	}
	
	@Rule
	public Destination destination = getDestination();
	
	private Destination getDestination() {
//			setup();

	    Destination destination = new Destination( this,"http://127.0.0.1:8080/");
	    RequestContext context = destination.getRequestContext();
	    context.addPathSegment("id", "tester@test.com");
	    return destination;
	}
	
	@Context
	private Response response; // will be injected after every request

	@HttpTest( method = Method.GET, path = "/services/user/{id}" )
    public void testGet() throws Exception {
    	
    	assertOk(response);
    	
    }
	
    /**
     * TODO: add closed cycle test POST -> GET
     * @throws Exception
     */
    @HttpTest(method=Method.POST, path = "/services/user", type = MediaType.APPLICATION_JSON, content = "{\"email\" : \"post_tester@test.com\",  \"password\" : \"pass1234\"}")
    public void testPost() throws Exception {
        assertEquals(201, response.getStatus());
    }

    @HttpTest(method=Method.POST, path = "/services/user/auth", type = MediaType.APPLICATION_JSON, content = "{\"email\" : \"tester@test.com\",  \"password\" : \"12345\"}")
    public void testAuth() throws Exception {
    	assertOk(response);
    }

    @HttpTest(method=Method.POST, path = "/services/user/auth", type = MediaType.APPLICATION_JSON, content = "{\"email\" : \"tester@test.com\",  \"password\" : \"1235\"}")
    public void testAuthBadPassword() throws Exception {
    	assertEquals(403, response.getStatus());
    }

    @HttpTest(method=Method.POST, path = "/services/user/auth", type = MediaType.APPLICATION_JSON, content = "{\"email\" : \"tester@tst.com\",  \"password\" : \"12345\"}")
    public void testAuthBadEmail() throws Exception {
    	assertEquals(403, response.getStatus());
    }

    @HttpTest(method=Method.POST, path = "/services/user/auth", type = MediaType.APPLICATION_JSON, content = "{\"email\" : \"tester@tst.com\",  \"password\" : \"1245\"}")
    public void testAuthBadEmailAndPassword() throws Exception {
    	assertEquals(403, response.getStatus());
    }
}
