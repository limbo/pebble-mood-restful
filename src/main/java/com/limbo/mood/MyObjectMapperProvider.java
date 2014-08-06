package com.limbo.mood;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

@Provider
public class MyObjectMapperProvider implements ContextResolver<ObjectMapper> {

    final ObjectMapper defaultObjectMapper;

    public MyObjectMapperProvider() {
        defaultObjectMapper = new ObjectMapper();
        defaultObjectMapper.configure(Feature.INDENT_OUTPUT, true);
    }

	public ObjectMapper getContext(Class<?> type) {
        return defaultObjectMapper;
	}

}
