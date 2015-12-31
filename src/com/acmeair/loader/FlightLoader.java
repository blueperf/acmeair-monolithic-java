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
package com.acmeair.loader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import com.acmeair.AcmeAirConstants;
import com.acmeair.service.FlightService;
import com.acmeair.service.ServiceLocator;




public class FlightLoader implements AcmeAirConstants {
	

	private FlightService flightService = ServiceLocator.instance().getService(FlightService.class);

	public void loadFlights(int maxDaysToScheduleFlights) throws Exception {
		InputStream csvInputStream = FlightLoader.class.getResourceAsStream("/mileage.csv");
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(csvInputStream));
		
		//Drop DB to reinitialize
		flightService.dropDB();
		
		String line1 = lnr.readLine();
		String line2 = lnr.readLine();
		StringTokenizer st1 = new StringTokenizer(line1, ",");
		StringTokenizer st2 = new StringTokenizer(line2, ",");
		List<String> airports = new ArrayList<String>();
		// Airport Mapping is not used at all, but putting them in DB anyway
		while (st1.hasMoreTokens()) {
			String name = st1.nextToken();
			String code = st2.nextToken();
			airports.add(code);
			if(logger.isLoggable(Level.FINE)){
				logger.fine("FlightLoader: airport code - " + code + " name - " + name );
			}
			flightService.createAirportCodeMapping(code, name);
		}
		
		// read the other lines which are of format:
		// airport name, aiport code, distance from this airport to whatever airport is in the column from lines one and two
		// Airport Mapping is not used at all, but putting them in DB anyway
		String line;
		int flightNumber = 0;
		while (true) {
			line = lnr.readLine();
			if (line == null || line.trim().equals("")) {
				break;
			}
			StringTokenizer st = new StringTokenizer(line, ",");
			String name = st.nextToken();
			String code = st.nextToken();
			airports.add(code);
			if(logger.isLoggable(Level.FINE)){
				logger.fine("FlightLoader: airport code - " + code + " name - " + name + "Does exist? - " + airports.contains(code));
			}
			if(!airports.contains(code)){
				flightService.createAirportCodeMapping(code, name);
			}
			
			int indexIntoTopLine = 0;
			while (st.hasMoreTokens()) {
				String milesString = st.nextToken();
				if (milesString.equals("NA")) {
					indexIntoTopLine++;
					continue;
				}
				String toAirport = airports.get(indexIntoTopLine);
				String flightId = "AA" + flightNumber;
				if(logger.isLoggable(Level.FINE)){
					logger.fine("FlightLoader: FlightSegments - " + flightId + " "+ code + " "+ toAirport + " "+ milesString);
				}				
				flightService.storeFlightSegment(flightId, code, toAirport, milesString);
				Date now = new Date();
				
				for (int daysFromNow = 0; daysFromNow < maxDaysToScheduleFlights; daysFromNow++) {
					Calendar c = Calendar.getInstance();
					c.setTime(now);
					c.set(Calendar.HOUR_OF_DAY, 0);
				    c.set(Calendar.MINUTE, 0);
				    c.set(Calendar.SECOND, 0);
				    c.set(Calendar.MILLISECOND, 0);
					c.add(Calendar.DATE, daysFromNow);
					Date departureTime = c.getTime();
					Date arrivalTime = getArrivalTime(departureTime, Integer.parseInt(milesString));
					flightService.createNewFlight(flightId, departureTime, arrivalTime, 500, 200, 10, 200, "B747");
					
				}
				flightNumber++;
				indexIntoTopLine++;
			}
		}
		lnr.close();
	}
	
	private static Date getArrivalTime(Date departureTime, int mileage) {
		double averageSpeed = 600.0; // 600 miles/hours
		double hours = (double) mileage / averageSpeed; // miles / miles/hour = hours
		double partsOfHour = hours % 1.0;
		int minutes = (int)(60.0 * partsOfHour);
		Calendar c = Calendar.getInstance();
		c.setTime(departureTime);
		c.add(Calendar.HOUR, (int)hours);
		c.add(Calendar.MINUTE, minutes);
		return c.getTime();
	}
}
