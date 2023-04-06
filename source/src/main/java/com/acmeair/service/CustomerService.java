/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.service;

import jakarta.inject.Inject;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.acmeair.web.dto.CustomerInfo;

public abstract class CustomerService {
  protected static final int DAYS_TO_ALLOW_SESSION = 1;

  @Inject
  protected KeyGenerator keyGenerator;

  public abstract void createCustomer(String username, String password, String status,
      int total_miles, int miles_ytd, String phoneNumber, String phoneNumberType,
      String addressJson);

  public abstract String createAddress(String streetAddress1, String streetAddress2, String city,
      String stateProvince, String country, String postalCode);

  public abstract void updateCustomer(String username, CustomerInfo customerJson);

  protected abstract String getCustomer(String username);

  public abstract String getCustomerByUsername(String username);

  public boolean validateCustomer(String username, String password) {
    boolean validatedCustomer = false;
    String customerToValidate = getCustomer(username);
    if (customerToValidate != null) {
      try {
        JSONObject customerJson = (JSONObject) new JSONParser().parse(customerToValidate);
        validatedCustomer = password.equals((String) customerJson.get("password"));
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return validatedCustomer;
  }

  public String getCustomerByUsernameAndPassword(String username, String password) {
    String c = getCustomer(username);
    try {
      JSONObject customerJson = (JSONObject) new JSONParser().parse(c);
      if (!customerJson.get("password").equals(password)) {
        return null;
      }
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
    // Should we also set the password to null?
    return c;
  }

  public abstract Long count();

  public abstract void dropCustomers();

}
