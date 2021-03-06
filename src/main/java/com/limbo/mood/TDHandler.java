package com.limbo.mood;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.jetty.server.handler.ContextHandler;

import com.treasure_data.client.TreasureDataClient;

/**
 * Test class to use Traeasure Data. No longer in use.
 * TODO: remove this class and other references to TD.
 * @author limbo
 *
 */
public class TDHandler {

	private static final TreasureDataClient tdClient = initTdClient();
	
	private static TreasureDataClient initTdClient() {
    	Properties props = System.getProperties();
    	try {
//    		ContextHandler context = new ContextHandler();
    		 URL r = ContextHandler.getCurrentContext().getResource("/WEB-INF/treasure-data.properties");
    		 props.load(r.openStream());
//	        props.load(ContextHandler.getCurrentContext().getResourceAsStream("WEB-INF/treasure-data.properties"));
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return new TreasureDataClient(props);
	}
	
	public static TreasureDataClient getTdClient() {
		return tdClient;
	}

}
