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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.acmeair.service.CustomerService;
import com.acmeair.service.FlightService;
import com.acmeair.service.ServiceLocator;
import com.acmeair.web.dto.CustomerInfo;

@Path("/customer")
public class CustomerREST {
	
	private CustomerService customerService = ServiceLocator.instance().getService(CustomerService.class);
	
	@Context 
	private HttpServletRequest request;


	private boolean validate(String customerid)	{
		String loginUser = (String) request.getAttribute(RESTCookieSessionFilter.LOGIN_USER);
		if(logger.isLoggable(Level.FINE)){
			logger.fine("validate : loginUser " + loginUser + " customerid " + customerid);
		}
		return customerid.equals(loginUser);
	}
	
	protected Logger logger =  Logger.getLogger(FlightService.class.getName());

	@GET
	@Path("/byid/{custid}")
	@Produces("text/plain")
	public Response getCustomer(@CookieParam("sessionid") String sessionid, @PathParam("custid") String customerid) {
		if(logger.isLoggable(Level.FINE)){
			logger.fine("getCustomer : session ID " + sessionid + " userid " + customerid);
		}
		try {
			// make sure the user isn't trying to update a customer other than the one currently logged in
			if (!validate(customerid)) {
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			return Response.ok(customerService.getCustomerByUsername(customerid)).build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@POST
	@Path("/byid/{custid}")
	@Produces("text/plain")
	public /* Customer */ Response putCustomer(@CookieParam("sessionid") String sessionid, CustomerInfo customer) {
		String username = customer.getUsername();
		if (!validate(username)) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		String customerFromDB = customerService.getCustomerByUsernameAndPassword(username, customer.getPassword());
		if(logger.isLoggable(Level.FINE)){
			logger.fine("putCustomer : " + customerFromDB);
		}

		if (customerFromDB == null) {
			// either the customer doesn't exist or the password is wrong
			return Response.status(Response.Status.FORBIDDEN).build();
		}
		
		customerService.updateCustomer(username, customer);
		
		//Retrieve the latest results
		customerFromDB = customerService.getCustomerByUsernameAndPassword(username, customer.getPassword());
		return Response.ok(customerFromDB).build();
	}
	

	
}
