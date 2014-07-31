package com.example.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.example.MongoHQHandler;
import com.example.models.Rating;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteResult;

@Path("/rating/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class RatingsService {

	@GET
	public Response get(@PathParam("id") String id) {
		Response r;
		DB db = MongoHQHandler.getDB();
		
		System.err.println("GET RATING: " + id);
		
		DBObject q = QueryBuilder.start("_id").is(new ObjectId(id)).get();
		System.err.println("QUERY: " + q.toString());
		
		DBObject obj = db.getCollection("mood-ratings").findOne(q);
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
    public Response put(Rating rating) {

    	DB db = MongoHQHandler.getDB();

    	DBObject newRating = new BasicDBObject();
    	newRating.put("time", rating.getTimeCreated());
    	newRating.put("rating", rating.getRating());
    	newRating.put("locations", rating.getLocations());
    	newRating.put("tags1", rating.getTags(0));
    	newRating.put("tags2", rating.getTags(1));
    	newRating.put("tags3", rating.getTags(2));
    	newRating.put("tags4", rating.getTags(3));
    	newRating.put("person1", rating.getPerson(0));
    	newRating.put("person2", rating.getPerson(1));
    	newRating.put("person3", rating.getPerson(2));
    	newRating.put("person4", rating.getPerson(3));

    	Response r;
    	WriteResult result = db.getCollection("mood-ratings").insert(newRating);
    	if (result.getError() != null) {
        	System.err.println("RATING POST ERR: " + result.getError());
        	r = Response.serverError().status(500).build();
    	} else {
    		r= Response.created(null).build();
    	}
    	return r;
    }

}

