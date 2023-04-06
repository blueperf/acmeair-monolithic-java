/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.loader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.acmeair.service.CustomerService;

@ApplicationScoped
public class CustomerLoader {

  @Inject
  CustomerService customerService;

  public void dropCustomers() {
    System.out.println(customerService);
    customerService.dropCustomers();
  }

  public void loadCustomers(long numCustomers) {

    String addressJson =
        "{streetAddress1 : \"123 Main St.\", streetAddress2 :null, city: \"Anytown\", stateProvince: \"NC\", country: \"USA\", postalCode: \"27617\"}";

    for (long ii = 0; ii < numCustomers; ii++) {
      customerService.createCustomer("uid" + ii + "@email.com", "password", "GOLD", 1000000, 1000,
          "919-123-4567", "BUSINESS", addressJson);
    }
  }

}
