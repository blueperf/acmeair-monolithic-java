/*******************************************************************************
* Copyright (c) 2013 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
package com.acmeair.web;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.acmeair.service.FlightService;

@Path("/flights")
public class FlightsREST {
	
	@Inject
	private FlightService flightService;
	
	// TODO:  Consider a pure GET implementation of this service, but maybe not much value due to infrequent similar searches
	@POST
	@Path("/queryflights")
	@Consumes({"application/x-www-form-urlencoded"})
	@Produces("text/plain")
	public String getTripFlights(
			@FormParam("fromAirport") String fromAirport,
			@FormParam("toAirport") String toAirport,
			@FormParam("fromDate") DateParam fromDate,
		      @FormParam("returnDate") DateParam returnDate,
			@FormParam("oneWay") boolean oneWay
			) {
		
		String options = "";
		
		List<String> toFlights = flightService.getFlightByAirportsAndDepartureDate(fromAirport, toAirport, fromDate.getDate());
		
		if (!oneWay) {
			List<String> retFlights = flightService.getFlightByAirportsAndDepartureDate(toAirport, fromAirport, returnDate.getDate());

			options = "{\"tripFlights\":" + 
					"[{\"numPages\":1,\"flightsOptions\": " + toFlights + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}, " + 
					"{\"numPages\":1,\"flightsOptions\": " + retFlights + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}], " + 
					"\"tripLegs\":2}";
		}
		else {
			options = "{\"tripFlights\":" + 
					"[{\"numPages\":1,\"flightsOptions\": " + toFlights + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}], " + 
					"\"tripLegs\":1}";
		}
		
		return options;
	}

}