/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.service;

import java.util.List;

public interface BookingService {

  String bookFlight(String customerId, String flightSegmentId, String FlightId);

  String getBooking(String user, String id);

  List<String> getBookingsByUser(String user);

  void cancelBooking(String user, String id);

  Long count();

  void dropBookings();

  String getServiceType();
}
