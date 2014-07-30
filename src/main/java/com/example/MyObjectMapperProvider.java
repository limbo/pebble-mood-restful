package com.example;

import javax.ws.rs.ext.ContextResolver;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

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
