package com.example;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.example.models.Rating;

public class MoodApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> classes = new HashSet<Class<?>>();

        // register root resources
        classes.add(Rating.class);

        // register Jackson ObjectMapper resolver
        classes.add(MyObjectMapperProvider.class);

        return classes;
    }

}
