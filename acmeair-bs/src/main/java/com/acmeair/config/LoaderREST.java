package com.acmeair.config;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.acmeair.loader.Loader;


@Path("/bookings/loader")
public class LoaderREST {

	//	private static Logger logger = Logger.getLogger(LoaderREST.class.getName());

	@Inject
	private Loader loader;	
	
	@GET
	@Path("/load")
	@Produces("text/plain")
	public Response loadDB() {	
		String response = loader.clearBookingDB();
		return Response.ok(response).build();	
	}

}
