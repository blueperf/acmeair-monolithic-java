package com.acmeair.config;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import com.acmeair.loader.Loader;


@Path("/info/loader")
public class LoaderREST {

//	private static Logger logger = Logger.getLogger(LoaderREST.class.getName());

	
/*
 * Disabling to test out the new acmeair code frist
 */

	@Inject
	private Loader loader;	
	
	@GET
	@Path("/query")
	@Produces("text/plain")
	public Response queryLoader() {			
		String response = loader.queryLoader();
		return Response.ok(response).build();	
	}
	
	
	@GET
	@Path("/load")
	@Produces("text/plain")
	public Response loadDB(@DefaultValue("-1") @QueryParam("numCustomers") long numCustomers) {	
		String response = loader.loadDB(numCustomers);
		return Response.ok(response).build();	
	}

}
