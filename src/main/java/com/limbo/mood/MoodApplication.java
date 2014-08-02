package com.limbo.mood;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.limbo.mood.services.RatingsService;
import com.limbo.mood.services.UserService;

public class MoodApplication extends Application {

	public MoodApplication() {
		super();

	}
	@Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> classes = new HashSet<Class<?>>();

        // register root resources
        classes.add(RatingsService.class);
        classes.add(UserService.class);

        // register Jackson ObjectMapper resolver
        classes.add(MyObjectMapperProvider.class);

        return classes;
    }

}
