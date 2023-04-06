/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.mongo.services;

import static com.mongodb.client.model.Filters.eq;
import java.util.Date;
import org.bson.Document;
import com.acmeair.mongo.ConnectionManager;
import com.acmeair.service.AuthService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthServiceImpl extends AuthService implements  com.acmeair.mongo.Constants {

  private MongoCollection<Document> customerSession;

  @PostConstruct
  public void initialization() {
    MongoDatabase database = ConnectionManager.getConnectionManager().getDB();
    customerSession = database.getCollection("customerSession");
  }

  @Override
  public Long countSessions() {
    return customerSession.countDocuments();
  }

  @Override
  protected String getSession(String sessionid) {
    return customerSession.find(eq("_id", sessionid)).first().toJson();
  }

  @Override
  protected void removeSession(String sessionJson) {
    new Document();
    customerSession.deleteMany(Document.parse(sessionJson));
  }

  @Override
  protected String createSession(String sessionId, String customerId, Date creation,
      Date expiration) {
    Document sessionDoc = new Document("_id", sessionId).append("customerid", customerId)
        .append("lastAccessedTime", creation).append("timeoutTime", expiration);

    customerSession.insertOne(sessionDoc);

    return sessionDoc.toJson();

  }

  @Override
  public void invalidateSession(String sessionid) {
    customerSession.deleteMany(eq("_id", sessionid));
  }

  @Override
  public void dropSessions() {
    customerSession.deleteMany(new Document());
  }
}
