package com.acmeair.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MicroServiceFilter implements Filter {
	
	private static final String CUSTOMER_BYID_PATH = "/rest/api/customer/byid/";
	private static final String BOOKINGS_BYUSER_PATH = "/rest/api/bookings/byuser/";
	private static final String BOOKINGS_BOOK_PATH = "/rest/api/bookings/bookflights";
	private static final String BOOKINGS_CANCEL_PATH = "/rest/api/bookings/cancelbooking";
	private static final String FLIGHTS_QUERY_PATH = "/rest/api/flights/queryflights";
	
	
	static final String customerServiceLocation = System.getenv("CUSTOMER_SERVICE");
	static final String customerContextRoot = "/acmeair-cs";
	
	static final String flightBookingServiceLocation = System.getenv("FLIGHTBOOKING_SERVICE");
	static final String flightBookingContextRoot = "/acmeair-fbs";
	

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		String path = request.getServletPath() + request.getPathInfo();

		// Customer Service Filter 
		if (path.startsWith(CUSTOMER_BYID_PATH) && request.getMethod()=="GET" ||
			path.startsWith(CUSTOMER_BYID_PATH) && request.getMethod()=="POST"	) {
			
			if (customerServiceLocation != null && customerServiceLocation != "") {		
				String serviceLoc = customerServiceLocation + customerContextRoot;
				doHttpServiceCall(serviceLoc, request,response);
				return;
			}
		} 
		
		// FlightBooking Service Filter 
		if (path.startsWith(BOOKINGS_BYUSER_PATH) && request.getMethod()=="GET" ||
			path.startsWith(BOOKINGS_CANCEL_PATH) && request.getMethod()=="POST" ||
			path.startsWith(BOOKINGS_BOOK_PATH) && request.getMethod()=="POST" ||
			path.startsWith(FLIGHTS_QUERY_PATH) && request.getMethod()=="POST") {
			
			if (flightBookingServiceLocation != null && flightBookingServiceLocation != "") {
				String serviceLoc = flightBookingServiceLocation + flightBookingContextRoot;
				doHttpServiceCall(serviceLoc, request,response);
				return;
			}
			
		} 

		// No micro-service, continue on with chain
		chain.doFilter(req, resp);
		
	}

	/** 
		Makes a URLConnection to the JAX-RS Service, doing a Get or Post
		There may be better client JAX-RS APIs for this?
		Also, Error handling is not good.
	**/
	private void doHttpServiceCall(String serviceLoc, HttpServletRequest request, HttpServletResponse response) throws MalformedURLException, IOException
	{
		
		String path = request.getServletPath() + request.getPathInfo();
		String sessionId = getSessionId(request);
		
		HttpURLConnection urlc = (HttpURLConnection) new URL("http://"+ serviceLoc + path).openConnection();
		urlc.setRequestProperty("Cookie", LoginREST.SESSIONID_COOKIE_NAME + "=" + sessionId);
		
		// A little ExtraWork if Post
		if (request.getMethod()=="POST") {
			urlc.setDoOutput(true); // Triggers POST.
			
			urlc.setRequestProperty("Content-Type", request.getContentType());
			

			// Get contents from the post and forward it to the micro-service
			try (OutputStream output = urlc.getOutputStream()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
				StringBuffer sb = new StringBuffer();
				String l = null;
				while ((l=br.readLine())!=null) {
					sb=sb.append(l);
				}
				br.close(); 
				
				output.write(sb.toString().getBytes());
			}		
		}
						
		// Get result from the micro-service
		BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String l = null;
		while ((l=br.readLine())!=null) {
			sb=sb.append(l);
		}
		
		br.close();
    					
		// Write response to client.
		if (urlc.getResponseCode() == 200) {
			
			final OutputStream outputStream = response.getOutputStream();

			outputStream.write(sb.toString().getBytes());
			outputStream.close();					
		} 
		return;
	}
	
	// Gets the session id from the cookie to forward to the service request.
	// There may be a better way to do security than this...
	private String getSessionId(HttpServletRequest request) {
		
		Cookie cookies[] = request.getCookies();
		Cookie sessionCookie = null;
		String sessionId = "";
		
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals(LoginREST.SESSIONID_COOKIE_NAME)) {
					sessionCookie = c;
				}
				if (sessionCookie!=null)
					break; 
			}
			
			if (sessionCookie!=null) // We need both cookie to work
				sessionId= sessionCookie.getValue().trim();
		}
		
		return sessionId;	
	}

	@Override
	public void destroy() {		
	}
	
	

}
