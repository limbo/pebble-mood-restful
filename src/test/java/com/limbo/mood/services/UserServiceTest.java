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
public class UserServiceTest extends AbstractServiceTest {
	@Context
	public Response response; // will be injected after every request

	@BeforeClass
	public static void setupUserServiceTest() {
		setup();
		
    	testID = createTestUser();
    	assertNotNull(testID);
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
