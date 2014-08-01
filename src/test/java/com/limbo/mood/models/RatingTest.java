package com.limbo.mood.models;

import org.junit.Test;

import com.limbo.mood.models.Rating;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class RatingTest {
    
    @Test
    public void testEmptyConstructor() throws Exception {
        final Rating rating = new Rating();
        assertEquals(-1, rating.getRating());
    }

    @Test
    public void testGoodConstructor() throws Exception {
        final Rating rating = new Rating(5, "testplace", 1l, 2l, "test1", "test2", null, null, null, "tester1", "tester2", "tester3", null, null);
        assertEquals(5, rating.getRating());
        assertEquals("testplace", rating.getLocations());
        assertEquals("test1", rating.getTags(0));
        assertEquals("test2", rating.getTags(1));
        assertEquals(null, rating.getTags(2));
        assertEquals(null, rating.getTags(3));
        assertEquals(null, rating.getTags(4));
        assertEquals("tester1", rating.getPerson(0));
        assertEquals("tester2", rating.getPerson(1));
        assertEquals("tester3", rating.getPerson(2));
        assertEquals(null, rating.getPerson(3));
        assertEquals(null, rating.getPerson(4));
        assertEquals(1l, rating.getLongitude());
        assertEquals(12, rating.getLatitude());
    }

    @Test
    public void testSkippingConstructor() throws Exception {
        final Rating rating = new Rating(5, "testplace", 1l, 2l, "test1", "test2", null, "test3", null, null, "tester2", "tester3", null, null);
        assertEquals(5, rating.getRating());
        assertEquals("testplace", rating.getLocations());
        assertEquals("test1", rating.getTags(0));
        assertEquals("test2", rating.getTags(1));
        assertEquals("test3", rating.getTags(2));
        assertEquals(null, rating.getTags(3));
        assertEquals(null, rating.getTags(4));
        assertEquals("tester2", rating.getPerson(0));
        assertEquals("tester3", rating.getPerson(1));
        assertEquals(null, rating.getPerson(2));
        assertEquals(null, rating.getPerson(3));
        assertEquals(null, rating.getPerson(4));
    }
}
