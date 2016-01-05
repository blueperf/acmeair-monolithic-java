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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.acmeair.service.CustomerService;
import com.acmeair.service.ServiceLocator;


import javax.ws.rs.core.Context;

import org.json.simple.JSONObject;

@Path("/authtoken")
public class AuthRest {
	
	private CustomerService customerService = ServiceLocator.instance().getService(CustomerService.class);
	
	@Context 
	private HttpServletRequest request;
	
	@POST
	@Path("/byuserid/{user}")
	@Produces("application/json")
	public Response createSession(@PathParam("user") String user) {
		try {
			JSONObject sessionJson = customerService.createSession(user);
			return Response.ok(sessionJson.toString()).build();	
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/{tokenid}")
	@Produces("application/json")
	public Response validateSession(@PathParam("tokenid") String tokenid) {
				
		JSONObject sessionJson = customerService.validateSession(tokenid);
		String customerid = (String) sessionJson.get("customerid");
		
		if (customerid == null) {
			return null;
		} else {
			return Response.ok(sessionJson.toString()).build();
		}
		
	}
	
	@DELETE
	@Path("/{tokenid}")
	@Produces("application/json")
	public void invalidateSession(@PathParam("tokenid") String tokenid) {
		customerService.invalidateSession(tokenid);
	}
	

	
}
