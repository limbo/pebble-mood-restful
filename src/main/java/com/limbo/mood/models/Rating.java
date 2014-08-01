package com.limbo.mood.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class Rating {

	@JsonProperty("rating")
	private final int rating;
	@JsonProperty("locations")
	private final String locations;
	@JsonProperty("longitude")
	private final long longitude;
	@JsonProperty("lat")
	private final long latitude;
	@JsonProperty("tags")
	private final String[] tags;
	@JsonProperty("people")
	private final String[] people;
	@JsonProperty("time_created")
	private final Date time_created = new Date();

	public Rating() {
		rating = -1;
		locations = new String();
		tags = new String[0];
		people = new String[0];
		longitude = -1;
		latitude = -1;
	}
	
	private String[] collectElements(String e1, String e2, String e3, String e4, String e5) {
    	ArrayList<String> t = new ArrayList<String>(Arrays.asList(new String[]{e1, e2, e3, e4, e5}));
    	t.removeAll(Collections.singleton(null));
    	return t.toArray(new String[t.size()]);
	}
	
    public Rating(int rating, String locations, long longitude, long latitude, String tag1, String tag2, String tag3, String tag4, String tag5, String person1, String person2, String person3, String person4, String person5) {
    	this.rating = rating;
    	this.locations = locations;
    	this.tags = collectElements(tag1, tag2, tag3, tag4, tag5);
    	this.people = collectElements(person1, person2, person3, person4, person5);
    	this.longitude = longitude;
    	this.latitude = latitude;
    }

    public Date getTimeCreated() {
    	return time_created;
    }
    
	public int getRating() {
		return rating;
	}

	public String getLocations() {
		return locations;
	}

	public String[] getTags() {
		return tags;
	}
	
	public String getTags(int i) {
		if (tags.length > i) {
			return tags[i];
		} else {
			return null;
		}
	}

	public String[] getPeople() {
		return people;
	}
	
	public String getPerson(int i) {
		if (people.length > i) {
			return people[i];
		} else {
			return null;
		}
	}

	public long getLongitude() {
		return longitude;
	}

	public long getLatitude() {
		return latitude;
	}
}
