package com.limbo.mood;

import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Provider
public class AccessControlResponseFilter implements ContainerResponseFilter {

	@Override
	public ContainerResponse filter(ContainerRequest requestContext, ContainerResponse responseContext) {
		responseContext.getHttpHeaders().add("Access-Control-Allow-Origin", "*");
		responseContext.getHttpHeaders().add("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type");
		responseContext.getHttpHeaders().add("Access-Control-Expose-Headers", "Location, Content-Disposition");
		responseContext.getHttpHeaders().add("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, HEAD, OPTIONS");
		
		return responseContext;
	}

}
