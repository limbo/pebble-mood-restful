package com.example.models;

import org.junit.Test;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class RatingTest {
    
    @Test
    public void testEmptyConstructor() throws Exception {
        final Rating rating = new Rating();
        assertEquals(-1, rating.getRating());
    }

}
