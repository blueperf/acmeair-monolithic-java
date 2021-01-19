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

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

import jakarta.ws.rs.core.Response;

import org.json.simple.JSONObject;

import com.acmeair.service.AuthService;
import com.acmeair.service.CustomerService;


@Path("/api/login")
public class LoginREST {
	
	public static String SESSIONID_COOKIE_NAME = "acmeair_sessionid";
			
	@Inject
	AuthService authService;

	@Inject
	CustomerService customerService;
	
	
	@POST
	@Consumes({"application/x-www-form-urlencoded"})
	@Produces("text/plain")
	public Response login(@FormParam("login") String login, @FormParam("password") String password) {
		try {

			boolean validCustomer = customerService.validateCustomer(login, password);
			
			if (!validCustomer) {
				return Response.status(Response.Status.FORBIDDEN).build();
			}	
			
			JSONObject sessionJson = authService.createSession(login);
	
			return Response.ok("logged in").header("Set-Cookie", SESSIONID_COOKIE_NAME + "=" + sessionJson.get("_id") + "; Path=/").build();
	
		}
		catch (Exception e) {
			e.printStackTrace();
      		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GET
	@Path("/logout")
	@Produces("text/plain")
	public Response logout(@QueryParam("login") String login, @CookieParam("acmeair_sessionid") String sessionid) {
		try {
			if (sessionid == null) {
				System.out.println("sessionid is null");
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			if (sessionid.equals(""))
			{
				System.out.println("sessionid is empty");
			} else {
				authService.invalidateSession(sessionid);
			}

			return Response.ok("logged out").build();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
