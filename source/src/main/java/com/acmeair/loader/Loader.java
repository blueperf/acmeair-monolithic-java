/*
 * (C) Copyright IBM Corp. 2013, 2023
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.acmeair.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Loader {

  @Inject
  FlightLoader flightLoader;
  @Inject
  CustomerLoader customerLoader;
  @Inject
  SessionLoader sessionLoader;
  @Inject
  BookingLoader bookingLoader;

  public static String REPOSITORY_LOOKUP_KEY = "com.acmeair.repository.type";

  private static Logger logger = Logger.getLogger(Loader.class.getName());

  public String queryLoader() {
    String message = System.getProperty("loader.numCustomers");
    if (message == null) {
      logger.info(
          "The system property 'loader.numCustomers' has not been set yet. Looking up the default properties.");
      lookupDefaults();
      message = System.getProperty("loader.numCustomers");
    }
    return message;
  }

  public String loadDB(long numCustomers) {
    String message = "";
    if (numCustomers == -1)
      message = execute();
    else {
      System.setProperty("loader.numCustomers", Long.toString(numCustomers));
      message = execute(numCustomers);
    }
    return message;
  }

  public String loadCustomerDB(long numCustomers) {
    String message = "";

    System.setProperty("loader.numCustomers", Long.toString(numCustomers));
    message = executeCustomerDB(numCustomers);

    return message;
  }

  public String loadFlightDB() {
    String message = "";
    message = executeFlightDB();
    return message;
  }

  public String clearSessionDB() {
    String message = "";

    message = executeSessionDB();

    return message;
  }

  public String clearBookingDB() {
    String message = "";
    message = executeBookingDB();
    return message;
  }

  public static void main(String args[]) throws Exception {
    Loader loader = new Loader();
    loader.execute();
  }

  private String execute() {
    String numCustomers = System.getProperty("loader.numCustomers");
    if (numCustomers == null) {
      logger.info(
          "The system property 'loader.numCustomers' has not been set yet. Looking up the default properties.");
      lookupDefaults();
      numCustomers = System.getProperty("loader.numCustomers");
    }
    return execute(Long.parseLong(numCustomers));
  }

  private String execute(long numCustomers) {

    double length = 0;
    try {
      long start = System.currentTimeMillis();
      logger.info("Start loading flights");
      customerLoader.dropCustomers();
      flightLoader.dropFlights();
      sessionLoader.dropSessions();
      bookingLoader.dropBookings();

      flightLoader.loadFlights();
      logger.info("Start loading " + numCustomers + " customers");
      customerLoader.loadCustomers(numCustomers);
      long stop = System.currentTimeMillis();
      logger.info("Finished loading in " + (stop - start) / 1000.0 + " seconds");
      length = (stop - start) / 1000.0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "Loaded flights and " + numCustomers + " customers in " + length + " seconds";
  }

  private String executeCustomerDB(long numCustomers) {
    CustomerLoader customerLoader = new CustomerLoader();

    double length = 0;
    try {
      long start = System.currentTimeMillis();
      logger.info("Start loading " + numCustomers + " customers");
      customerLoader.dropCustomers();
      customerLoader.loadCustomers(numCustomers);
      long stop = System.currentTimeMillis();
      logger.info("Finished loading in " + (stop - start) / 1000.0 + " seconds");
      length = (stop - start) / 1000.0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "Loaded " + numCustomers + " customers in " + length + " seconds";
  }

  private String executeFlightDB() {
    FlightLoader flightLoader = new FlightLoader();

    double length = 0;
    try {
      long start = System.currentTimeMillis();
      logger.info("Start loading flights");
      flightLoader.dropFlights();
      flightLoader.loadFlights();
      long stop = System.currentTimeMillis();
      logger.info("Finished loading in " + (stop - start) / 1000.0 + " seconds");
      length = (stop - start) / 1000.0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "Loaded flights in " + length + " seconds";
  }

  private String executeSessionDB() {
    SessionLoader sessionLoader = new SessionLoader();

    double length = 0;
    try {
      long start = System.currentTimeMillis();
      logger.info("Start clearing session");
      sessionLoader.dropSessions();
      long stop = System.currentTimeMillis();
      logger.info("Finished clearing in " + (stop - start) / 1000.0 + " seconds");
      length = (stop - start) / 1000.0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "Cleared sessions in " + length + " seconds";
  }

  private String executeBookingDB() {
    BookingLoader bookingLoader = new BookingLoader();

    double length = 0;
    try {
      long start = System.currentTimeMillis();
      logger.info("Start clearing session");
      bookingLoader.dropBookings();
      long stop = System.currentTimeMillis();
      logger.info("Finished clearing in " + (stop - start) / 1000.0 + " seconds");
      length = (stop - start) / 1000.0;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "Cleared sessions in " + length + " seconds";
  }

  private void lookupDefaults() {
    Properties props = getProperties();

    String numCustomers = props.getProperty("loader.numCustomers", "100");
    System.setProperty("loader.numCustomers", numCustomers);
  }

  private Properties getProperties() {
    /*
     * Get Properties from loader.properties file. If the file does not exist, use default values
     */
    Properties props = new Properties();
    String propFileName = "/loader.properties";
    try {
      InputStream propFileStream = Loader.class.getResourceAsStream(propFileName);
      props.load(propFileStream);
      // props.load(new FileInputStream(propFileName));
    } catch (FileNotFoundException e) {
      logger.info("Property file " + propFileName + " not found.");
    } catch (IOException e) {
      logger.info("IOException - Property file " + propFileName + " not found.");
    }
    return props;
  }
  /*
   * private void execute(String args[]) { ApplicationContext ctx = null; // // Get Properties from
   * loader.properties file. // If the file does not exist, use default values // Properties props =
   * new Properties(); String propFileName = "/loader.properties"; try{ InputStream propFileStream =
   * Loader.class.getResourceAsStream(propFileName); props.load(propFileStream); // props.load(new
   * FileInputStream(propFileName)); }catch(FileNotFoundException e){ logger.info("Property file " +
   * propFileName + " not found."); }catch(IOException e){
   * logger.info("IOException - Property file " + propFileName + " not found."); }
   * 
   * String numCustomers = props.getProperty("loader.numCustomers","100");
   * System.setProperty("loader.numCustomers", numCustomers);
   * 
   * String type = null; String lookup = REPOSITORY_LOOKUP_KEY.replace('.', '/');
   * jakarta.naming.Context context = null; jakarta.naming.Context envContext; try { context = new
   * jakarta.naming.InitialContext(); envContext = (jakarta.naming.Context)
   * context.lookup("java:comp/env"); if (envContext != null) type = (String)
   * envContext.lookup(lookup); } catch (NamingException e) { // e.printStackTrace(); }
   * 
   * if (type != null) { logger.info("Found repository in web.xml:" + type); } else if (context !=
   * null) { try { type = (String) context.lookup(lookup); if (type != null)
   * logger.info("Found repository in server.xml:" + type); } catch (NamingException e) { //
   * e.printStackTrace(); } }
   * 
   * if (type == null) { type = System.getProperty(REPOSITORY_LOOKUP_KEY); if (type != null)
   * logger.info("Found repository in jvm property:" + type); else { type =
   * System.getenv(REPOSITORY_LOOKUP_KEY); if (type != null)
   * logger.info("Found repository in environment property:" + type); } }
   * 
   * if (type ==null) // Default to wxsdirect { type = "wxsdirect";
   * logger.info("Using default repository :" + type); } if (type.equals("wxsdirect")) ctx = new
   * AnnotationConfigApplicationContext(WXSDirectAppConfig.class); else if
   * (type.equals("mongodirect")) ctx = new
   * AnnotationConfigApplicationContext(MongoDirectAppConfig.class); else { logger.
   * info("Did not find a matching config. Using default repository wxsdirect instead" ); ctx = new
   * AnnotationConfigApplicationContext(WXSDirectAppConfig.class); }
   * 
   * FlightLoader flightLoader = ctx.getBean(FlightLoader.class); CustomerLoader customerLoader =
   * ctx.getBean(CustomerLoader.class);
   * 
   * try { long start = System.currentTimeMillis(); logger.info("Start loading flights");
   * flightLoader.loadFlights(); logger.info("Start loading " + numCustomers + " customers");
   * customerLoader.loadCustomers(Long.parseLong(numCustomers)); long stop =
   * System.currentTimeMillis(); logger.info("Finished loading in " + (stop - start)/1000.0 +
   * " seconds"); } catch (Exception e) { e.printStackTrace(); } }
   */
}
