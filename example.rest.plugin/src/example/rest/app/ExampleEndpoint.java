package example.rest.app;

import java.time.LocalDateTime;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.ibm.domino.osgi.core.context.ContextInfo;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

/*

I need someone who can help create a Domino 11 Java 8+ OSGi servlet plugin+feature+updatesite that can be deployed to a Domino 11 server.

It should have the following endpoints:
- GET|POST /path/database.nsf/api/endpoint_a
- GET|POST /path/database.nsf/api/endpoint_b

endpoint_a should return a simple response with the signed in users name encoded as JSON using Gson.
endpoint_b should return a simple response with the signed in users name encoded as JSON using Gson, and it should also switch to an 'administrator' user inside the servlet for looking up documents in a pseudo database that the user normally does not have access to but which the 'administrator' has. It should return a combined JSON response with the signed users name as well as some kind of indication that the 'administrator' lookup was performed using another Notes user.

Deliverables:
- Servlet plugin (the actual codebase)
- Feature (packaging the servlet plugin)
- Update site (packaging of the features)
- Documentation of how to setup development environment that can then import the deliveries, make some changes and recompile to an updated updatesite.
- Documentation of how to enable updatesites on a Domino 11 server and how to deploy the delivered servlet/updatesite into it, and what needs to be done in order to activate it.

PS. Using Gson for JSON parsing/generating might need to be deployed separately in another updatesite and used by the above mentioned servlet.

 
 */


/**
 * This is a sample REST resource invoked by the `example` path.
 * 
 * The "api" part is not configurable. 
 * The "example" part should be configured in the plugin.xml.
 * and here in the code.  
 * 
 * http://{DominoServer}/api/example/endpoint_a
 * http://{DominoServer}/{DatabasePath}/api/example/endpoint_a
 *  
 */
@Path("/example")
public class ExampleEndpoint {

	/**
	 * This is a sample response.
	 * It is required by the GSON library to serialize it as JSON.
	 * If you prefer constructing JSON responses dynamically 
	 * I recommend using the JSON-P library:
	 * https://javaee.github.io/jsonp/ 
	 */
	@SuppressWarnings("unused")
	private static class Resp1 {
		String currentServer;
		String currentDatabase;
		String commonName;
		String effectiveName;
		String debugMethodName;

		Resp1(Session session) throws NotesException {
			this.commonName = session.getCommonUserName();
			this.effectiveName = session.getEffectiveUserName();
			this.currentServer = session.getServerName();
			Database db = ContextInfo.getUserDatabase();
			if (db != null) {
				this.currentDatabase = db.getFilePath();
			}
		}
	}
	
	/**
	 * This is a sample response from endpoint_b call.
	 */
	@SuppressWarnings("unused")
	private static class Resp2 extends Resp1 {
		String signerName;
		String fieldFromDoc;

		Resp2(Session session) throws NotesException {
			super(session);
		}
	}

	/**
	 * A sample endpoint to test the library is working.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String root() {
		return "Example library " + LocalDateTime.now();
	}

	@Path("endpoint_a")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response endpointA() throws NotesException {
		// create a user session
		Session session = ContextInfo.getUserSession();
		// create a class with the response
		Resp1 resp = new Resp1(session);
		resp.debugMethodName = "endpoint_a - GET";
		// serialize the response to the JSON
		Gson gson = new Gson();
		String json = gson.toJson(resp);
		// return the response
		return Response.ok()
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(json)
				.build();
	}
	
	@Path("endpoint_a")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response endpointAPost() throws NotesException {
		// create a user session
		Session session = ContextInfo.getUserSession();
		// create a class with the response
		Resp1 name = new Resp1(session);
		name.debugMethodName = "endpoint_a - POST";
		// serialize the response to the JSON
		Gson gson = new Gson();
		String json = gson.toJson(name);
		// return the response
		return Response.ok()
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(json)
				.build();
	}
	

	/**
	 * Creates a response for the second endpoint.
	 */
	private Resp2 getResponseB() throws NotesException {
		Session session = ContextInfo.getUserSession();
		Resp2 resp = new Resp2(session);
		// create the session with the server rights
		Session nativeSession = NotesUtils.createSession();
		if (nativeSession != null) {
			resp.signerName = nativeSession.getCommonUserName();
			// get names.nsf as server
			Database names = nativeSession.getDatabase(session.getServerName(), "names.nsf");
			// read the ShortName field from the first document
			View view = names.getView("$people");
			Document doc = view.getFirstDocument();
			String shortName = doc.getItemValueString("ShortName");
			resp.fieldFromDoc = shortName;
		}
		return resp;
	}

	@Path("endpoint_b")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response endpointB() throws NotesException {
		Resp2 resp = getResponseB();
		resp.debugMethodName = "endpoint_b - GET";
		Gson gson = new Gson();
		String json = gson.toJson(resp);
        return Response.ok()
			.type(MediaType.APPLICATION_JSON_TYPE)
			.entity(json)
			.build();
	}

	
	@Path("endpoint_b")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response endpointBPost() throws NotesException {
		Resp2 resp = getResponseB();
		resp.debugMethodName = "endpoint_b - POST";
		Gson gson = new Gson();
		String json = gson.toJson(resp);
        return Response.ok()
			.type(MediaType.APPLICATION_JSON_TYPE)
			.entity(json)
			.build();
	}
	
}
