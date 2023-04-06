/*
 * (C) Copyright IBM Corp. 2015, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.mongo.services;

import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.acmeair.AirportCodeMapping;
import com.acmeair.mongo.ConnectionManager;
import com.acmeair.service.FlightService;
import com.acmeair.service.KeyGenerator;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FlightServiceImpl extends FlightService implements com.acmeair.mongo.Constants {

  private MongoCollection<Document> flight;
  private MongoCollection<Document> flightSegment;
  private MongoCollection<Document> airportCodeMapping;

  @Inject
  KeyGenerator keyGenerator;

  @PostConstruct
  public void initialization() {
    MongoDatabase database = ConnectionManager.getConnectionManager().getDB();
    flight = database.getCollection("flight");
    flightSegment = database.getCollection("flightSegment");
    airportCodeMapping = database.getCollection("airportCodeMapping");
  }

  @Override
  public Long countFlights() {
    return flight.countDocuments();
  }

  @Override
  public Long countFlightSegments() {
    return flightSegment.countDocuments();
  }

  @Override
  public Long countAirports() {
    return airportCodeMapping.countDocuments();
  }

  protected String getFlight(String flightId, String segmentId) {
    return flight.find(eq("_id", flightId)).first().toJson();
  }

  @Override
  protected String getFlightSegment(String fromAirport, String toAirport) {
    try {
      return flightSegment
          .find(new BasicDBObject("originPort", fromAirport).append("destPort", toAirport)).first()
          .toJson();
    } catch (java.lang.NullPointerException e) {
      if (logger.isLoggable(Level.FINE)) {
        logger.fine("getFlghSegment returned no flightSegment available");
      }
      return "";
    }
  }

  @Override
  protected List<String> getFlightBySegment(String segment, Date deptDate) {
    try {
      JSONObject segmentJson = (JSONObject) new JSONParser().parse(segment);
      MongoCursor<Document> cursor;

      if (deptDate != null) {
        if (logger.isLoggable(Level.FINE)) {
          logger.fine("getFlghtBySegment Search String : "
              + new BasicDBObject("flightSegmentId", segmentJson.get("_id"))
                  .append("scheduledDepartureTime", deptDate).toJson());
        }
        cursor = flight.find(new BasicDBObject("flightSegmentId", segmentJson.get("_id"))
            .append("scheduledDepartureTime", deptDate)).iterator();
      } else {
        cursor = flight.find(eq("flightSegmentId", segmentJson.get("_id"))).iterator();
      }

      List<String> flights = new ArrayList<String>();
      try {
        while (cursor.hasNext()) {
          Document tempDoc = cursor.next();

          if (logger.isLoggable(Level.FINE)) {
            logger.fine("getFlghtBySegment Before : " + tempDoc.toJson());
          }

          Date deptTime = (Date) tempDoc.get("scheduledDepartureTime");
          Date arvTime = (Date) tempDoc.get("scheduledArrivalTime");
          tempDoc.remove("scheduledDepartureTime");
          tempDoc.append("scheduledDepartureTime", deptTime.toString());
          tempDoc.remove("scheduledArrivalTime");
          tempDoc.append("scheduledArrivalTime", arvTime.toString());

          if (logger.isLoggable(Level.FINE)) {
            logger.fine("getFlghtBySegment after : " + tempDoc.toJson());
          }

          flights.add(tempDoc.append("flightSegment", segmentJson).toJson());
        }
      } finally {
        cursor.close();
      }
      return flights;
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void storeAirportMapping(AirportCodeMapping mapping) {
    Document airportDoc = new Document("_id", mapping.getAirportCode()).append("airportName",
        mapping.getAirportName());
    airportCodeMapping.insertOne(airportDoc);
  }

  @Override
  public AirportCodeMapping createAirportCodeMapping(String airportCode, String airportName) {
    return new AirportCodeMapping(airportCode, airportName);
  }

  @Override
  public void createNewFlight(String flightSegmentId, Date scheduledDepartureTime,
      Date scheduledArrivalTime, int firstClassBaseCost, int economyClassBaseCost,
      int numFirstClassSeats, int numEconomyClassSeats, String airplaneTypeId) {
    String id = keyGenerator.generate().toString();
    Document flightDoc = new Document("_id", id).append("firstClassBaseCost", firstClassBaseCost)
        .append("economyClassBaseCost", economyClassBaseCost)
        .append("numFirstClassSeats", numFirstClassSeats)
        .append("numEconomyClassSeats", numEconomyClassSeats)
        .append("airplaneTypeId", airplaneTypeId).append("flightSegmentId", flightSegmentId)
        .append("scheduledDepartureTime", scheduledDepartureTime)
        .append("scheduledArrivalTime", scheduledArrivalTime);

    flight.insertOne(flightDoc);
  }

  @Override
  public void storeFlightSegment(String flightSeg) {
    try {
      JSONObject flightSegJson = (JSONObject) new JSONParser().parse(flightSeg);
      storeFlightSegment((String) flightSegJson.get("_id"),
          (String) flightSegJson.get("originPort"), (String) flightSegJson.get("destPort"),
          (int) flightSegJson.get("miles"));
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void storeFlightSegment(String flightName, String origPort, String destPort, int miles) {
    Document flightSegmentDoc = new Document("_id", flightName).append("originPort", origPort)
        .append("destPort", destPort).append("miles", miles);

    flightSegment.insertOne(flightSegmentDoc);
  }

  @Override
  public void dropFlights() {
    airportCodeMapping.deleteMany(new Document());
    flightSegment.deleteMany(new Document());
    flight.deleteMany(new Document());
  }
}
