package com.example.services;

import com.example.models.Rating;
import com.treasure_data.client.ClientException;
import com.treasure_data.client.TreasureDataClient;
import com.treasure_data.model.Database;
import com.treasure_data.model.Job;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rating")
@Produces(MediaType.APPLICATION_JSON)
public class RatingsService {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void put(Rating rating) {
    	TreasureDataClient tdClient = new TreasureDataClient();
    	String query = "insert into table ratings now() as time, " + rating.getRating() + "," + rating.getLocations() + ",0,0," + 
    			rating.getTags(0) + "," + rating.getTags(1) + "," + rating.getTags(2) + "," + rating.getTags(3) + "," +  
    			rating.getPerson(0) + "," + rating.getPerson(1) + "," + rating.getPerson(2) + "," + rating.getPerson(3) + ";" ; 
    	Job j = new Job(new Database("mood"), query);
    	try {
			tdClient.submitJob(j);
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}

