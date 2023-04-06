/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.mongo;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ServerDescription;

public class ConnectionManager implements Constants {
  private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());

  private static AtomicReference<ConnectionManager> connectionManager =
      new AtomicReference<ConnectionManager>();

  protected MongoClient cli;
  protected MongoDatabase db;

  /**
   * Get the connection manager
   * 
   * @return
   */
  public static ConnectionManager getConnectionManager() {
    if (connectionManager.get() == null) {
      synchronized (connectionManager) {
        if (connectionManager.get() == null) {
          connectionManager.set(new ConnectionManager());
        }
      }
    }
    return connectionManager.get();
  }

  private ConnectionManager() {
    String hostname = getEnvironmentDefault("MONGO_HOST","localhost");
    int port = getEnvironmentDefault("MONGO_PORT", 27017);
    String dbname = getEnvironmentDefault("MONGO_DBNAME", "acmeair");

    // Credentials
    String user = System.getenv("MONGO_USER");
    String pass = "" + System.getenv("MONGO_PASSWORD"); // Avoid a NPE
    
    //@implNote set maxPoolSize=100 at this point
    ConnectionString cs = new ConnectionString("mongodb://" + hostname + ":" + port);
    try {
      
      MongoClientSettings.Builder builder = MongoClientSettings.builder();
      MongoClientSettings settings;
      if (user != null) {
        MongoCredential credential = MongoCredential.createPlainCredential(user, dbname, pass.toCharArray());
        builder.credential(credential);
        
        settings = MongoClientSettings.builder()
            .credential(credential)
            .applyConnectionString(cs)
            .build();
      } else { 
        settings = MongoClientSettings.builder()
            .applyConnectionString(cs)
            .build();
      }
      cli = MongoClients.create(settings);

      

      LOGGER.fine("#### Mongo DB Database Name " + dbname + " ####");
      db = cli.getDatabase(dbname);
      ServerDescription desc = cli.getClusterDescription().getServerDescriptions().get(0);
      LOGGER.info("#### Mongo DB Hosts " + desc.getHosts() + " ####");
      LOGGER.info("#### Mongo DB is created with DB name " + dbname + " ####");
    } catch (Exception e) {
      LOGGER.severe("Caught Exception : " + e.getMessage());
    }

  }
  
  private String getEnvironmentDefault(String valName, String def) { 
    String val = System.getenv(valName);
    if (val == null) { 
      return def;
    }
    return val;
 
  }
  
  private int getEnvironmentDefault(String valName, int def) { 
    String val = System.getenv(valName);
    
    try {
      return Integer.parseInt(val);
    } catch (NullPointerException | NumberFormatException nfe) {
      LOGGER.severe("NFE was invalid " + def);
      return def;
    }
  }

  public MongoDatabase getDB() {
    return db;
  }
}
