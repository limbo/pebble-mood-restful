package com.example.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Rating {

	private final int rating;
	private final String locations;
	private final String[] tags;
	private final String[] people;

	public Rating() {
		rating = -1;
		locations = new String();
		tags = new String[0];
		people = new String[0];
	}
	
    public Rating(int rating, String locations, String tag1, String tag2, String tag3, String tag4, String tag5, String person1, String person2, String person3, String person4, String person5) {
    	this.rating = rating;
    	this.locations = locations;
    	this.tags = new String[]{tag1, tag2, tag3, tag4};
    	this.people = new String[]{person1, person2, person3, person4};
    }

	public int getRating() {
		return rating;
	}

	public String getLocations() {
		return locations;
	}

	public String getTags(int i) {
		return tags[i];
	}

	public String getPerson(int i) {
		return people[i];
	}

}
