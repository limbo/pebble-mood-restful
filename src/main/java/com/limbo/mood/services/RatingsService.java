package com.limbo.mood.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.limbo.mood.MongoHQHandler;
import com.limbo.mood.models.Rating;
import com.limbo.mood.models.User;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;

@Path("/rating")
@Produces(MediaType.APPLICATION_JSON)
public class RatingsService {

	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") String id) {
		System.err.println("Get Rating");
		Response r;
		
		System.err.println("GET RATING: " + id);
		
		DBObject obj = MongoHQHandler.findById(Rating.getCollectionName(), id);
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
	public Response report(@PathParam("period") String period, @QueryParam("authtoken") String authToken) {
		Response r;
		
		System.err.println("Report: " + period);
		System.err.println("token: " + authToken);
		
		// verify authentication and get user id
		String currentUserId = authenticateToken(authToken);
		if (currentUserId == null) {
			r = Response.status(401).build();
			return r;
		}
		System.err.println("CURRENT user: " + currentUserId);
		
		// build report
		// TODO: find a library to make aggregation nicer.
		// TODO: extract query handling into separate class.
		DBObject owner = new BasicDBObject("owner_id", currentUserId);
		DBObject lastYear = new BasicDBObject("time", new BasicDBObject("$gt", yearAgo(new Date())));
		ArrayList<DBObject> clauses = new ArrayList<DBObject>();
		clauses.add(owner);
		clauses.add(lastYear);
		DBObject match = new BasicDBObject("$match", new BasicDBObject("$and", clauses));
		if (period.equalsIgnoreCase("day")) {
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
			AggregationOutput output = MongoHQHandler.getCollection(Rating.getCollectionName()).aggregate(pipeline);
			
			r = Response.ok(output.results()).build();
			
		} else if (period.equalsIgnoreCase("week")) {
			r = Response.ok("week").build();
			DBObject week = new BasicDBObject("year", new BasicDBObject("$year", "$time"));
			week.put("week", new BasicDBObject("$week", "$time"));
			DBObject groupFields = new BasicDBObject("_id", week);
			groupFields.put("average", new BasicDBObject("$avg", "$rating"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			System.err.println("MATCH:" + match.toString());
			System.err.println("GROUP: " + groupFields.toString());
			
			DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
			
			List<DBObject> pipeline = Arrays.asList(match, group, sort);
			AggregationOutput output = MongoHQHandler.getCollection(Rating.getCollectionName()).aggregate(pipeline);
			
			r = Response.ok(output.results()).build();

			
		} else if (period.equalsIgnoreCase("month")) {
			DBObject month = new BasicDBObject("year", new BasicDBObject("$year", "$time"));
			month.put("month", new BasicDBObject("$month", "$time"));
			DBObject groupFields = new BasicDBObject("_id", month);
			groupFields.put("average", new BasicDBObject("$avg", "$rating"));
			DBObject group = new BasicDBObject("$group", groupFields);
			
			System.err.println("MATCH:" + match.toString());
			System.err.println("GROUP: " + groupFields.toString());
			
			DBObject sort = new BasicDBObject("$sort", new BasicDBObject("_id", 1));
			
			List<DBObject> pipeline = Arrays.asList(match, group, sort);
			AggregationOutput output = MongoHQHandler.getCollection(Rating.getCollectionName()).aggregate(pipeline);
			
			r = Response.ok(output.results()).build();
			
		} else {
			r = Response.status(Status.NOT_ACCEPTABLE).build();			
		}
		
		return r;
	}

	private String authenticateToken(String authToken) {
		if (authToken == null || authToken.equals("")) {
			return null;
		} else {
			DBObject q = QueryBuilder.start("authtoken").is(authToken).get();
			System.err.println("QUERY: " + q.toString());
			
			DBObject u = MongoHQHandler.getCollection(User.getCollectionName()).findOne(q);
			if (u == null) {
				return null;
			} else {
				return u.get("_id").toString();
			}
		}
	}
	
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@Context UriInfo uriInfo, Rating rating, @QueryParam("authtoken") String authToken) {
    	Response r;

		// verify authentication and get user id
		String currentUserId = authenticateToken(authToken);
		if (currentUserId == null) {
			r = Response.status(401).build();
			return r;
		}
		System.err.println("CURRENT user: " + currentUserId);

		// build new DBObject
		// TODO: check out objectbuilder in mongoDB lib
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
    	newRating.put("owner_id", currentUserId);

    	try {
    		String id = MongoHQHandler.insert(Rating.getCollectionName(), newRating, true);
    		
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

