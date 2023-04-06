/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.loader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.acmeair.service.BookingService;

@ApplicationScoped
public class BookingLoader {

  @Inject
  BookingService bookingService;

  public void dropBookings() {
    bookingService.dropBookings();
  }

}
