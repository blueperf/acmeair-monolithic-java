/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.mongo.services;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import org.bson.Document;
import com.acmeair.mongo.ConnectionManager;
import com.acmeair.service.CustomerService;
import com.acmeair.web.dto.CustomerInfo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerServiceImpl extends CustomerService implements com.acmeair.mongo.Constants {

  private MongoCollection<Document> customer;

  @PostConstruct
  public void initialization() {
    MongoDatabase database = ConnectionManager.getConnectionManager().getDB();
    customer = database.getCollection("customer");
  }

  @Override
  public Long count() {
    return customer.countDocuments();
  }

  @Override
  public void createCustomer(String username, String password, String status, int total_miles,
      int miles_ytd, String phoneNumber, String phoneNumberType, String addressJson) {

    new Document();
    Document customerDoc = new Document("_id", username).append("password", password)
        .append("status", status).append("total_miles", total_miles).append("miles_ytd", miles_ytd)
        .append("address", Document.parse(addressJson)).append("phoneNumber", phoneNumber)
        .append("phoneNumberType", phoneNumberType);

    customer.insertOne(customerDoc);
  }

  @Override
  public String createAddress(String streetAddress1, String streetAddress2, String city,
      String stateProvince, String country, String postalCode) {
    Document addressDoc = new Document("streetAddress1", streetAddress1).append("city", city)
        .append("stateProvince", stateProvince).append("country", country)
        .append("postalCode", postalCode);
    if (streetAddress2 != null) {
      addressDoc.append("streetAddress2", streetAddress2);
    }
    return addressDoc.toJson();
  }

  @Override
  public void updateCustomer(String username, CustomerInfo customerInfo) {

    Document address = new Document("streetAddress1", customerInfo.getAddress().getStreetAddress1())
        .append("city", customerInfo.getAddress().getCity())
        .append("stateProvince", customerInfo.getAddress().getStateProvince())
        .append("country", customerInfo.getAddress().getCountry())
        .append("postalCode", customerInfo.getAddress().getPostalCode());

    if (customerInfo.getAddress().getStreetAddress2() != null) {
      address.append("streetAddress2", customerInfo.getAddress().getStreetAddress2());
    }
    customer.updateOne(eq("_id", customerInfo.get_id()),
        combine(set("status", customerInfo.getStatus()),
            set("total_miles", customerInfo.getTotal_miles()),
            set("miles_ytd", customerInfo.getMiles_ytd()), set("address", address),
            set("phoneNumber", customerInfo.getPhoneNumber()),
            set("phoneNumberType", customerInfo.getPhoneNumberType())));
  }

  @Override
  protected String getCustomer(String username) {
    return customer.find(eq("_id", username)).first().toJson();
  }

  @Override
  public String getCustomerByUsername(String username) {
    Document customerDoc = customer.find(eq("_id", username)).first();
    if (customerDoc != null) {
      customerDoc.remove("password");
      customerDoc.append("password", null);
    }
    return customerDoc.toJson();
  }

  @Override
  public void dropCustomers() {
    customer.deleteMany(new Document());

  }

}
