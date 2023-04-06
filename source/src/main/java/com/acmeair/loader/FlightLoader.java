/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.loader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.acmeair.service.FlightService;

import com.acmeair.AirportCodeMapping;

@ApplicationScoped
public class FlightLoader {

  private static final int MAX_FLIGHTS_PER_SEGMENT = 5;

  @Inject
  private FlightService flightService;

  public void dropFlights() {
    flightService.dropFlights();
  }

  public void loadFlights() throws Exception {
    InputStream csvInputStream = FlightLoader.class.getResourceAsStream("/mileage.csv");

    LineNumberReader lnr = new LineNumberReader(new InputStreamReader(csvInputStream));
    String line1 = lnr.readLine();
    StringTokenizer st = new StringTokenizer(line1, ",");
    ArrayList<AirportCodeMapping> airports = new ArrayList<AirportCodeMapping>();

    // read the first line which are airport names
    while (st.hasMoreTokens()) {
      AirportCodeMapping acm = flightService.createAirportCodeMapping(null, st.nextToken());
      // acm.setAirportName(st.nextToken());
      airports.add(acm);
    }
    // read the second line which contains matching airport codes for the first line
    String line2 = lnr.readLine();
    st = new StringTokenizer(line2, ",");
    int ii = 0;
    while (st.hasMoreTokens()) {
      String airportCode = st.nextToken();
      airports.get(ii).setAirportCode(airportCode);
      ii++;
    }
    // read the other lines which are of format:
    // airport name, aiport code, distance from this airport to whatever airport is
    // in the column from lines one and two
    String line;
    int flightNumber = 0;
    while (true) {
      line = lnr.readLine();
      if (line == null || line.trim().equals("")) {
        break;
      }
      st = new StringTokenizer(line, ",");
      String airportName = st.nextToken();
      String airportCode = st.nextToken();
      if (!alreadyInCollection(airportCode, airports)) {
        AirportCodeMapping acm = flightService.createAirportCodeMapping(airportCode, airportName);
        airports.add(acm);
      }
      int indexIntoTopLine = 0;
      while (st.hasMoreTokens()) {
        String milesString = st.nextToken();
        if (milesString.equals("NA")) {
          indexIntoTopLine++;
          continue;
        }
        int miles = Integer.parseInt(milesString);
        String toAirport = airports.get(indexIntoTopLine).getAirportCode();
        String flightId = "AA" + flightNumber;
        flightService.storeFlightSegment(flightId, airportCode, toAirport, miles);
        Date now = new Date();
        for (int daysFromNow = -1; daysFromNow < MAX_FLIGHTS_PER_SEGMENT; daysFromNow++) {
          Calendar c = Calendar.getInstance();
          c.setTime(now);
          c.set(Calendar.HOUR_OF_DAY, 0);
          c.set(Calendar.MINUTE, 0);
          c.set(Calendar.SECOND, 0);
          c.set(Calendar.MILLISECOND, 0);
          c.add(Calendar.DATE, daysFromNow);
          Date departureTime = c.getTime();
          Date arrivalTime = getArrivalTime(departureTime, miles);
          flightService.createNewFlight(flightId, departureTime, arrivalTime, 500, 200, 10, 200,
              "B747");

        }
        flightNumber++;
        indexIntoTopLine++;
      }
    }

    for (int jj = 0; jj < airports.size(); jj++) {
      flightService.storeAirportMapping(airports.get(jj));
    }
    lnr.close();
  }

  private static Date getArrivalTime(Date departureTime, int mileage) {
    double averageSpeed = 600.0; // 600 miles/hours
    double hours = (double) mileage / averageSpeed; // miles / miles/hour = hours
    double partsOfHour = hours % 1.0;
    int minutes = (int) (60.0 * partsOfHour);
    Calendar c = Calendar.getInstance();
    c.setTime(departureTime);
    c.add(Calendar.HOUR, (int) hours);
    c.add(Calendar.MINUTE, minutes);
    return c.getTime();
  }

  static private boolean alreadyInCollection(String airportCode,
      ArrayList<AirportCodeMapping> airports) {
    for (int ii = 0; ii < airports.size(); ii++) {
      if (airports.get(ii).getAirportCode().equals(airportCode)) {
        return true;
      }
    }
    return false;
  }
}
