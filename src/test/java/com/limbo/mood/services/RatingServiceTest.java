package com.limbo.mood.services;

import static com.eclipsesource.restfuse.Assert.assertOk;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

@RunWith(HttpJUnitRunner.class)
public class RatingServiceTest extends AbstractServiceTest {
	@Context
	public Response response; // will be injected after every request
    
	@BeforeClass
	public static void setupRatingServiceTest() {
		setup();
		createTestUserWithAuth();
    	testID = createTestRating();
    	
    	assertNotNull(testID);
	}

	@Rule
	public Destination destination = getDestination();
	
	private Destination getDestination() {
	    Destination destination = new Destination( this,"http://127.0.0.1:8080/");
	    RequestContext context = destination.getRequestContext();
	    context.addPathSegment("id", testID);
	    context.addPathSegment("authtoken", testAuthToken);
	    return destination;
	}
	
	@HttpTest( method = Method.GET, path = "/services/rating/{id}" )
    public void testGet() throws Exception {
    	
    	assertOk(response);
    	
    }
	
    @HttpTest(method=Method.GET, path = "/services/rating/report/day")
    public void testReportUnauthorized() throws Exception {
    	assertEquals(401, response.getStatus());
    }

    @HttpTest(method=Method.GET, path = "/services/rating/report/day?authtoken=testAuthTokenValu")
    public void testReportBadAuthToken() throws Exception {
    	assertEquals(401, response.getStatus());
    }

	@HttpTest(method=Method.GET, path = "/services/rating/report/day?authtoken={authtoken}")
    public void testReportDay() throws Exception {
    	assertOk(response);
    }

    @HttpTest(method=Method.GET, path = "/services/rating/report/week?authtoken={authtoken}")
    public void testReportWeek() throws Exception {
    	assertOk(response);
    }

    @HttpTest(method=Method.GET, path = "/services/rating/report/month?authtoken={authtoken}")
    public void testReportMonth() throws Exception {
    	assertOk(response);
    }
    
    /**
     * TODO: add closed cycle test POST -> GET
     * @throws Exception
     */
    @HttpTest(method=Method.POST, path = "/services/rating?authtoken={authtoken}", type = MediaType.APPLICATION_JSON, content = "{\"rating\" : 6,  \"longitude\" : -122.2774162889666,  \"lat\" : 37.81409106220846,  \"locations\" : \"home\",  \"tags\" : [\"test1\", \"test2\"], \"people\" : [\"tester1\", \"tester2\"]}")
    public void testPost() throws Exception {
        assertEquals(201, response.getStatus());
    }

    @HttpTest(method=Method.POST, path = "/services/rating", type = MediaType.APPLICATION_JSON, content = "{\"rating\" : 6,  \"longitude\" : -122.2774162889666,  \"lat\" : 37.81409106220846,  \"locations\" : \"home\",  \"tags\" : [\"test1\", \"test2\"], \"people\" : [\"tester1\", \"tester2\"]}")
    public void testPostUnauthorized() throws Exception {
        assertEquals(401, response.getStatus());
    }

    @HttpTest(method=Method.POST, path = "/services/rating?authtoken=testAuthTokenValu", type = MediaType.APPLICATION_JSON, content = "{\"rating\" : 6,  \"longitude\" : -122.2774162889666,  \"lat\" : 37.81409106220846,  \"locations\" : \"home\",  \"tags\" : [\"test1\", \"test2\"], \"people\" : [\"tester1\", \"tester2\"]}")
    public void testPostBadAuthToken() throws Exception {
        assertEquals(401, response.getStatus());
    }
}
