package com.limbo.mood;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.limbo.mood.services.RatingsService;
import com.treasure_data.client.TreasureDataClient;

public class MoodApplication extends Application {

	public MoodApplication() {
		super();

	}
	@Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> classes = new HashSet<Class<?>>();

        // register root resources
        classes.add(RatingsService.class);

        // register Jackson ObjectMapper resolver
        classes.add(MyObjectMapperProvider.class);

        return classes;
    }

}
