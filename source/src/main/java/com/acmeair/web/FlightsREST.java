/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.web;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import com.acmeair.service.FlightService;

@Path("/api/flights")
public class FlightsREST {

  @Inject
  private FlightService flightService;

  // TODO: Consider a pure GET implementation of this service, but maybe not much
  // value due to infrequent similar searches
  @POST
  @Path("/queryflights")
  @Consumes({"application/x-www-form-urlencoded"})
  @Produces("text/plain")
  public String getTripFlights(@FormParam("fromAirport") String fromAirport,
      @FormParam("toAirport") String toAirport, @FormParam("fromDate") DateParam fromDate,
      @FormParam("returnDate") DateParam returnDate, @FormParam("oneWay") boolean oneWay) {

    String options = "";

    List<String> toFlights = flightService.getFlightByAirportsAndDepartureDate(fromAirport,
        toAirport, fromDate.getDate());

    if (!oneWay) {
      List<String> retFlights = flightService.getFlightByAirportsAndDepartureDate(toAirport,
          fromAirport, returnDate.getDate());

      options = "{\"tripFlights\":" + "[{\"numPages\":1,\"flightsOptions\": " + toFlights
          + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}, "
          + "{\"numPages\":1,\"flightsOptions\": " + retFlights
          + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}], " + "\"tripLegs\":2}";
    } else {
      options = "{\"tripFlights\":" + "[{\"numPages\":1,\"flightsOptions\": " + toFlights
          + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}], " + "\"tripLegs\":1}";
    }

    return options;
  }

}
