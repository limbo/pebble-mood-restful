package com.limbo.mood.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.limbo.mood.MongoHQHandler;
import com.limbo.mood.models.Rating;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

@Path("/rating")
@Produces(MediaType.APPLICATION_JSON)
public class RatingsService {

	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") String id) {
		System.err.println("Get Rating");
		Response r;
		
		System.err.println("GET RATING: " + id);
		
		DBObject obj = MongoHQHandler.findById("mood-ratings", id);
		if (obj != null) {
			obj.removeField("_id");
			r = Response.ok(obj).build();
		} else {
			r = Response.serverError().status(404).build();
		}
		
		return r;
	}
		
	@GET
	@Path("/report/{period}")
	public Response report(@PathParam("period") String period) {
		Response r;
		
		System.err.println("Report: " + period);
		
		if (period.equalsIgnoreCase("day")) {
			DBObject lastYear = new BasicDBObject("time", new BasicDBObject("$gt", yearAgo(new Date())));
			DBObject match = new BasicDBObject("$match", lastYear);
			DBObject date = new BasicDBObject("year", new BasicDBObject("$year", "$time"));
			date.put("month", new BasicDBObject("$month", "$time"));
			date.put("day", new BasicDBObject("$dayOfMonth", "$time"));
			DBObject groupFields = new BasicDBObject("_id", date);
			groupFields.put("average", new BasicDBObject("$avg", "$rating"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			System.err.println("MATCH:" + match.toString());
			System.err.println("GROUP: " + groupFields.toString());
			
			DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
			
			List<DBObject> pipeline = Arrays.asList(match, group, sort);
			AggregationOutput output = MongoHQHandler.getCollection("mood-ratings").aggregate(pipeline);
			
			r = Response.ok(output.results()).build();
			
		} else if (period.equalsIgnoreCase("week")) {
			r = Response.ok("week").build();
			DBObject lastYear = new BasicDBObject("time", new BasicDBObject("$gt", yearAgo(new Date())));
			DBObject match = new BasicDBObject("$match", lastYear);
			DBObject week = new BasicDBObject("year", new BasicDBObject("$year", "$time"));
			week.put("week", new BasicDBObject("$week", "$time"));
			DBObject groupFields = new BasicDBObject("_id", week);
			groupFields.put("average", new BasicDBObject("$avg", "$rating"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			System.err.println("MATCH:" + match.toString());
			System.err.println("GROUP: " + groupFields.toString());
			
			DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
			
			List<DBObject> pipeline = Arrays.asList(match, group, sort);
			AggregationOutput output = MongoHQHandler.getCollection("mood-ratings").aggregate(pipeline);
			
			r = Response.ok(output.results()).build();

			
		} else if (period.equalsIgnoreCase("month")) {
			DBObject lastYear = new BasicDBObject("time", new BasicDBObject("$gt", yearAgo(new Date())));
			DBObject match = new BasicDBObject("$match", lastYear);
			DBObject month = new BasicDBObject("year", new BasicDBObject("$year", "$time"));
			month.put("month", new BasicDBObject("$month", "$time"));
			DBObject groupFields = new BasicDBObject("_id", month);
			groupFields.put("average", new BasicDBObject("$avg", "$rating"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			System.err.println("MATCH:" + match.toString());
			System.err.println("GROUP: " + groupFields.toString());
			
			DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
			
			List<DBObject> pipeline = Arrays.asList(match, group, sort);
			AggregationOutput output = MongoHQHandler.getCollection("mood-ratings").aggregate(pipeline);
			
			r = Response.ok(output.results()).build();
			
		} else {
			r = Response.status(Status.NOT_ACCEPTABLE).build();			
		}
		
		return r;
	}
	
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@Context UriInfo uriInfo, Rating rating) {

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
    	try {
    		String id = MongoHQHandler.insert("mood-ratings", newRating, true);
    		
    		URI uri = new URI(uriInfo.getAbsolutePath().toString() + "/" + id);
        	r = Response.created(uri).build();
     	} catch (MongoException e) {
        	System.err.println("RATING MONGO EX: " + e.getMessage());
        	r = Response.serverError().status(500).build();
    	} catch (URISyntaxException e) {
        	System.err.println("RATING POST URI EX: " + e.getMessage());
        	r = Response.serverError().status(500).build();
		}
    	return r;
    }

    // Utility methods
	private Date yearAgo(Date d) {
		// Use the Calendar class to subtract one day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
	}

}

