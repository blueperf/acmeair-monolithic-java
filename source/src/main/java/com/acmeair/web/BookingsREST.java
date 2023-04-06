/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.web;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.acmeair.service.BookingService;

@Path("/api/bookings")
public class BookingsREST {

  @Inject
  private BookingService bs;

  @POST
  @Consumes({"application/x-www-form-urlencoded"})
  @Path("/bookflights")
  @Produces("text/plain")
  public /* BookingInfo */ Response bookFlights(@FormParam("userid") String userid,
      @FormParam("toFlightId") String toFlightId, @FormParam("toFlightSegId") String toFlightSegId,
      @FormParam("retFlightId") String retFlightId,
      @FormParam("retFlightSegId") String retFlightSegId,
      @FormParam("oneWayFlight") boolean oneWay) {
    try {
      String bookingIdTo = bs.bookFlight(userid, toFlightSegId, toFlightId);

      String bookingInfo = "";

      String bookingIdReturn = null;
      if (!oneWay) {
        bookingIdReturn = bs.bookFlight(userid, retFlightSegId, retFlightId);
        bookingInfo = "{\"oneWay\":false,\"returnBookingId\":\"" + bookingIdReturn
            + "\",\"departBookingId\":\"" + bookingIdTo + "\"}";
      } else {
        bookingInfo = "{\"oneWay\":true,\"departBookingId\":\"" + bookingIdTo + "\"}";
      }
      return Response.ok(bookingInfo).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("/byuser/{user}")
  @Produces("text/plain")
  public Response getBookingsByUser(@PathParam("user") String user) {
    try {
      return Response.ok(bs.getBookingsByUser(user).toString()).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Consumes({"application/x-www-form-urlencoded"})
  @Path("/cancelbooking")
  @Produces("text/plain")
  public Response cancelBookingsByNumber(@FormParam("number") String number,
      @FormParam("userid") String userid) {
    try {
      bs.cancelBooking(userid, number);
      return Response.ok("booking " + number + " deleted.").build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  public Response status() {
    return Response.ok("OK").build();
  }

}
