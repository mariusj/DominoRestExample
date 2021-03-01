package example.rest.app;

import java.util.HashSet;
import java.util.Set;

import org.apache.wink.common.WinkApplication;

/**
 * This is the definition of the REST application. 
 *
 */
public class RESTApp extends WinkApplication {

	@Override
	public Set<Class<?>> getClasses() {		
		Set<Class<?>> classes = new HashSet<>();
		// add all classes here that provide REST endpoints for this application
		classes.add(ExampleEndpoint.class);
		return classes;
	}

}
