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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.acmeair.util.Util;

public class RESTCookieSessionFilter implements Filter {
	
	static final String LOGIN_USER = "acmeair.login_user";
	private static String authServiceLocation = ((System.getenv("AUTH_SERVICE") == null) ? Util.getServiceProxy() + "/auth/acmeair-as" : System.getenv("AUTH_SERVICE"));

	//private static String authServiceLocation = System.getenv("AUTH_SERVICE");
			
	private static final String AUTHCHECK_PATH = "/rest/api/login/authcheck/";
	private static final String VALIDATE_PATH = "/rest/api/customer/validateid";
	private static final String CONFIG_PATH = "/rest/api/customer/config";
	private static final String LOADER_PATH = "/rest/api/customer/loader";
	
	public static String SESSIONID_COOKIE_NAME = "sessionid";
		
	@Inject
	BeanManager beanManager;
	
	@Override
	public void destroy() {
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,	FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;	
		
		String path = request.getContextPath() + request.getServletPath() + request.getPathInfo();
	
		if (path.endsWith(VALIDATE_PATH) || path.contains(CONFIG_PATH) || path.contains(LOADER_PATH)) {
			// if validating id, let the request flow
			// TODO: need to secure this somehow probably
			chain.doFilter(req, resp);
			return;
		}
		
		Cookie cookies[] = request.getCookies();
		Cookie sessionCookie = null;

		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(SESSIONID_COOKIE_NAME)) {
					sessionCookie = c;
				}
				if (sessionCookie!=null)
					break; 
			}
			String sessionId = "";
			if (sessionCookie!=null) // We need both cookie to work
				sessionId= sessionCookie.getValue().trim();
			// did this check as the logout currently sets the cookie value to "" instead of aging it out
			// see comment in LogingREST.java
			if (sessionId.equals("")) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			if (authServiceLocation == null || authServiceLocation == "") {
				authServiceLocation = "localhost/acmeair";
			}
						
			/* TODO: The jaxrs client code seems to a lot of classloading slowing everything way down - why?
			 * For now, do simple http call below
			ClientBuilder cb = ClientBuilder.newBuilder();
			Client c = cb.build();		
			
			WebTarget t = c.target("http://" + authServiceLocation  + AUTHCHECK_PATH + sessionId);
			Builder builder = t.request();
			builder.accept("application/json");
			
			Response res = builder.get();
			String output = res.readEntity(String.class);       
			c.close();			        
	    	*/
			
			// Instead, do simple http call
						
			// Set maxConnections - this seems to help with keepalives/running out of sockets with a high load.
			if (System.getProperty("http.maxConnections")==null) {
				System.setProperty("http.maxConnections", "50");
			}
			String url = "http://" + authServiceLocation  + AUTHCHECK_PATH + sessionId;

			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer responseString = new StringBuffer();

			while ((line = in.readLine()) != null) {
				responseString.append(line);
			}
			in.close();
			conn.disconnect();  // Is this necessary?
			
			String output = responseString.toString();
	    					
			String loginUser=null;
			if (output != null) {
				
				JSONObject jsonObject = (JSONObject)JSONValue.parse(output);
				loginUser=(String) jsonObject.get("customerid");
				
				request.setAttribute(LOGIN_USER, loginUser);
				chain.doFilter(req, resp);
				
				return;
			} else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
		}
		
		// if we got here, we didn't detect the session cookie, so we need to return 404
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
}
