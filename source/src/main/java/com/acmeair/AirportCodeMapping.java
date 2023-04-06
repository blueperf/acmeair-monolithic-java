/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair;

// Not sure this is the right place for this class, or if we really need it.
public class AirportCodeMapping {

  private String _id;
  private String airportName;

  public AirportCodeMapping() {}

  public AirportCodeMapping(String airportCode, String airportName) {
    this._id = airportCode;
    this.airportName = airportName;
  }

  public String getAirportCode() {
    return _id;
  }

  public void setAirportCode(String airportCode) {
    this._id = airportCode;
  }

  public String getAirportName() {
    return airportName;
  }

  public void setAirportName(String airportName) {
    this.airportName = airportName;
  }

  public String toJson() {
    return "{ _id : \"" + _id + "\", airportName : \"" + airportName + "\"}";
  }

}
