/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import com.acmeair.service.CustomerService;
import com.acmeair.service.FlightService;
import com.acmeair.web.dto.CustomerInfo;

@Path("/api/customer")
public class CustomerREST {

  @Inject
  CustomerService customerService;

  @Context
  private HttpServletRequest request;

  private boolean validate(String customerid) {
    String loginUser = (String) request.getAttribute(RESTCookieSessionFilter.LOGIN_USER);
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("validate : loginUser " + loginUser + " customerid " + customerid);
    }
    return customerid.equals(loginUser);
  }

  protected Logger logger = Logger.getLogger(FlightService.class.getName());

  @GET
  @Path("/byid/{custid}")
  @Produces("text/plain")
  public Response getCustomer(@CookieParam("sessionid") String sessionid,
      @PathParam("custid") String customerid) {
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("getCustomer : session ID " + sessionid + " userid " + customerid);
    }
    try {
      // make sure the user isn't trying to update a customer other than the one
      // currently logged in
      if (!validate(customerid)) {
        return Response.status(Response.Status.FORBIDDEN).build();
      }
      return Response.ok(customerService.getCustomerByUsername(customerid)).build();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @POST
  @Path("/byid/{custid}")
  @Produces("text/plain")
  public /* Customer */ Response putCustomer(@CookieParam("sessionid") String sessionid,
      CustomerInfo customer) {

    String username = customer.get_id();

    if (!validate(username)) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    String customerFromDB =
        customerService.getCustomerByUsernameAndPassword(username, customer.getPassword());
    if (logger.isLoggable(Level.FINE)) {
      logger.fine("putCustomer : " + customerFromDB);
    }

    if (customerFromDB == null) {
      // either the customer doesn't exist or the password is wrong
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    customerService.updateCustomer(username, customer);

    // Retrieve the latest results
    customerFromDB =
        customerService.getCustomerByUsernameAndPassword(username, customer.getPassword());
    return Response.ok(customerFromDB).build();
  }

}
