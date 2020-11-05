package com.acmeair.config;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.acmeair.service.AuthService;
import com.acmeair.service.BookingService;
import com.acmeair.service.CustomerService;
import com.acmeair.service.FlightService;


@Path("/config")
public class AcmeAirConfiguration {
    
	private static Logger logger = Logger.getLogger(AcmeAirConfiguration.class.getName());

	@Inject
	private BookingService bs;

	@Inject
	private CustomerService customerService;

	@Inject
	private AuthService authService;

	@Inject
	private FlightService flightService;

	
    public AcmeAirConfiguration() {
        super();
    }	
		
	
	@GET
	@Path("/activeDataService")
	@Produces("application/json")
	public Response getActiveDataServiceInfo() {
		try {		
			logger.fine("Get active Data Service info");
			return  Response.ok(bs.getServiceType()).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.ok("Unknown").build();
		}
	}
	
	/**
    *  Get runtime info.
    */
    @GET
    @Path("/runtime")
    @Produces("application/json")
    public String getRuntimeInfo() {
      JsonBuilderFactory factory = Json.createBuilderFactory(null);
      JsonArray value = factory.createArrayBuilder()
        .add(factory.createObjectBuilder()
            .add("name", "Runtime")
            .add("description", "Java"))
        .add(factory.createObjectBuilder()
            .add("name", "Version")
            .add("description", System.getProperty("java.version")))
        .add(factory.createObjectBuilder()
            .add("name", "Vendor")
            .add("description", System.getProperty("java.vendor")))
        .build();
    
      return value.toString();
    }

	
	
	@GET
	@Path("/countBookings")
	@Produces("application/json")
	public Response countBookings() {
		try {
			Long count = bs.count();			
			return Response.ok(count).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.ok(-1).build();
		}
	}
	
	@GET
	@Path("/countCustomers")
	@Produces("application/json")
	public Response countCustomer() {
		try {
			Long customerCount = customerService.count();
			
			return Response.ok(customerCount).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.ok(-1).build();
		}
	}
	
	
	@GET
	@Path("/countSessions")
	@Produces("application/json")
	public Response countCustomerSessions() {
		try {
			Long customerCount = authService.countSessions();
			
			return Response.ok(customerCount).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.ok(-1).build();
		}
	}
	
	
	@GET
	@Path("/countFlights")
	@Produces("application/json")
	public Response countFlights() {
		try {
			Long count = flightService.countFlights();			
			return Response.ok(count).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.ok(-1).build();
		}
	}
	
	@GET
	@Path("/countFlightSegments")
	@Produces("application/json")
	public Response countFlightSegments() {
		try {
			Long count = flightService.countFlightSegments();			
			return Response.ok(count).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.ok(-1).build();
		}
	}
	
	@GET
	@Path("/countAirports")
	@Produces("application/json")
	public Response countAirports() {
		try {			
			Long count = flightService.countAirports();	
			return Response.ok(count).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.ok(-1).build();
		}
	}
	
}
