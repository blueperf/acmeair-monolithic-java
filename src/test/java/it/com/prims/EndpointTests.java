/*******************************************************************************
 * Copyright (c) 2017, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package it.com.prims;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.runtime.Network;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Cookie;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.json.simple.parser.ParseException;
import org.junit.runners.MethodSorters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EndpointTests {

  private static String BASE_URL;
  private static String BASE_URL_WITH_CONTEXT_ROOT;

  private static final String USERNAME = "uid0@email.com";
  private static final String PASSWORD = "password";
  private static final String PASSWORD_BAD = "passwordb";

  private static final String LOAD_ENDPOINT = "rest/info/loader/load";
  private static final String LOAD_RESPONSE = "Loaded flights and";

  private static final String QUERY_ENDPOINT = "rest/api/flights/queryflights";
  private static final String QUERY_RESPONSE = "\"destPort\" : \"LHR\"";

  private static final String LOGIN_ENDPOINT = "rest/api/login";
  private static final String LOGIN_RESPONSE = "logged in";

  private static final String LOGOUT_ENDPOINT = "rest/api/login/logout";
  private static final String LOGOUT_RESPONSE = "logged out";

  private static final String COUNT_SESSIONS_ENDPOINT = "rest/info/config/countSessions";
  private static final String COUNT_SESSIONS_RESPONSE_0 = "0";
  private static final String COUNT_SESSIONS_RESPONSE_1 = "1";

  private static final String CUSTOMER_ENDPOINT = "rest/api/customer/byid/";
  private static final String CUSTOMER_RESPONSE = "\"_id\" : \"" + USERNAME + "\"";
  private static final String CUSTOMER_RESPONSE_2 = "NOTNULL";
 
  private static final String CUSTOMER_UPDATE = "{ \"_id\" : \"" + USERNAME + "\", \"status\" : \"GOLD\", \"total_miles\" : 1000000, \"miles_ytd\" : 1000, \"address\" : { \"streetAddress1\" : \"123 Main St.\", \"streetAddress2\" : \"NOTNULL\", \"city\" : \"Anytown\", \"stateProvince\" : \"NC\", \"country\" : \"USA\", \"postalCode\" : \"27617\" }, \"phoneNumber\" : \"919-123-4567\", \"phoneNumberType\" : \"BUSINESS\", \"password\" : \"" + PASSWORD + "\" }";
  private static final String CUSTOMER_UPDATE_BAD = "{ \"_id\" : \"" + USERNAME + "\", \"status\" : \"GOLD\", \"total_miles\" : 1000000, \"miles_ytd\" : 1000, \"address\" : { \"streetAddress1\" : \"123 Main St.\", \"streetAddress2\" : \"NOTNULL\", \"city\" : \"Anytown\", \"stateProvince\" : \"NC\", \"country\" : \"USA\", \"postalCode\" : \"27617\" }, \"phoneNumber\" : \"919-123-4567\", \"phoneNumberType\" : \"BUSINESS\", \"password\" : \"" + PASSWORD + "b\" }";


  private static final String BOOKFLIGHT_ENDPOINT = "rest/api/bookings/bookflights";
  private static final String BOOKFLIGHT_RESPONSE ="\"oneWay\":true,\"departBookingId\"";

  private static final String COUNT_BOOKINGS_ENDPOINT = "rest/info/config/countBookings";
  private static final String COUNT_BOOKINGS_RESPONSE_0 = "0";
  private static final String COUNT_BOOKINGS_RESPONSE_1 = "1";

  private static final String CANCELFLIGHT_ENDPOINT = "rest/api/bookings/cancelbooking";
  private static final String CANCELFLIGHT_RESPONSE ="deleted.";

  private static final String GET_BOOKINGS_ENDPOINT = "rest/api/bookings/byuser";

  public static final String SESSIONID_COOKIE_NAME = "acmeair_sessionid";

  private static Cookie sessionCookie = null;
  private static String number = null;

  private static MongodExecutable mongodExe;
  private static MongodProcess mongod;
  private Client client;
  private static String date;

  @BeforeClass
  public static void oneTimeSetup() throws UnknownHostException, IOException {

    String port = System.getProperty("liberty.test.port");

    BASE_URL = "http://localhost:" + port + "/";
    BASE_URL_WITH_CONTEXT_ROOT = BASE_URL;

    IFeatureAwareVersion version = de.flapdoodle.embed.mongo.distribution.Versions
        .withFeatures(new GenericVersion("4.0.0"), Version.Main.PRODUCTION.getFeatures());

    MongodStarter starter = MongodStarter.getDefaultInstance();
    String bindIp = "localhost";
    int mongoPort = 27017;
    IMongodConfig mongodConfig = new MongodConfigBuilder().version(version)
        .net(new Net(bindIp, mongoPort, Network.localhostIsIPv6())).build();
    mongodExe = starter.prepare(mongodConfig);
    mongod = mongodExe.start();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE MMM dd");

    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime nowChanged = now.toLocalDate().atStartOfDay(now.getZone());
    date = nowChanged.format(dtf) + " 00:00:00 UTC 2020";
  }

  @Before
  public void setup() {
    client = ClientBuilder.newClient();
    client.register(JsrJsonpProvider.class);
  }

  @After
  public void teardown() {
    client.close();
  }

  @AfterClass
  public static void tearDownMongo() {
    if (mongod != null) {
      mongod.stop();
      mongodExe.stop();
    }
  }

  @Test
  public void test01_Load() {
    String url = BASE_URL_WITH_CONTEXT_ROOT + LOAD_ENDPOINT;
    doTest(url, Status.OK, LOAD_RESPONSE);
  }

  @Test
  public void test02_GetFlight() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + QUERY_ENDPOINT;

    WebTarget target = client.target(url);

    Form form = new Form();
    form.param("fromAirport", "CDG");
    form.param("toAirport", "LHR");
    form.param("oneWay", "true");
    form.param("fromDate", date);
    form.param("returnDate", date);

    Response response = target.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE),
        Response.class);
    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, Status.OK.getStatusCode(), response.getStatus());

    String flightData = response.readEntity(String.class);
    assertThat(flightData, containsString(QUERY_RESPONSE));

    response.close();
  }

  @Test
  public void test03_Login() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + LOGIN_ENDPOINT;

    WebTarget target = client.target(url);

    Form form = new Form();
    form.param("login", USERNAME);
    form.param("password", PASSWORD);

    Response response = target.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE),
        Response.class);
    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, Status.OK.getStatusCode(), response.getStatus());

    String result = response.readEntity(String.class);
    assertThat(result, containsString(LOGIN_RESPONSE));

    // get to use cookie for future requests
    sessionCookie = response.getCookies().get(SESSIONID_COOKIE_NAME).toCookie();

    response.close();
  }

  @Test
  public void test03_LoginBad() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + LOGIN_ENDPOINT;

    WebTarget target = client.target(url);

    Form form = new Form();
    form.param("login", USERNAME);
    form.param("password", PASSWORD_BAD);

    Response response = target.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE),
        Response.class);
    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, Status.FORBIDDEN.getStatusCode(), response.getStatus());    
    response.close();
  }

  @Test
  public void test04_countSessions() {
    String url = BASE_URL_WITH_CONTEXT_ROOT + COUNT_SESSIONS_ENDPOINT;
    doTest(url, Status.OK, COUNT_SESSIONS_RESPONSE_1);
  }

  @Test
  public void test05_getCustomerBad() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + CUSTOMER_ENDPOINT + "/" + USERNAME;

    WebTarget target = client.target(url);
    Response response = target.request().get();

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, Status.FORBIDDEN.getStatusCode(), response.getStatus());

    response.close();
  }

  @Test
  public void test05_getCustomer() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + CUSTOMER_ENDPOINT + "/" + USERNAME;

    WebTarget target = client.target(url);

    Response response = target.request().cookie(sessionCookie).get();

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, Status.OK.getStatusCode(), response.getStatus());

    String result = response.readEntity(String.class);
    assertThat(result, containsString(CUSTOMER_RESPONSE));

    response.close();
  }

  @Test
  public void test06_updateCustomer() throws InterruptedException, ParseException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + CUSTOMER_ENDPOINT + "/"+ USERNAME; 
    
    WebTarget target = client.target(url);
     
    Response response = target.request().cookie(sessionCookie).post(Entity.entity(CUSTOMER_UPDATE,MediaType.APPLICATION_JSON),Response.class);

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.OK.getStatusCode(), response.getStatus());

    
    String result = response.readEntity(String.class);
    assertThat(result, containsString(CUSTOMER_RESPONSE_2));
    
    response.close();
  }

  @Test
  public void test06_updateCustomerBad1() throws InterruptedException, ParseException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + CUSTOMER_ENDPOINT + "/"+ USERNAME; 
    
    WebTarget target = client.target(url);
     
    Response response = target.request().post(Entity.entity(CUSTOMER_UPDATE,MediaType.APPLICATION_JSON),Response.class);

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.FORBIDDEN.getStatusCode(), response.getStatus());

    response.close();
  }

  @Test
  public void test06_updateCustomerBad2() throws InterruptedException, ParseException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + CUSTOMER_ENDPOINT + "/"+ USERNAME; 
    
    WebTarget target = client.target(url);
     
    Response response = target.request().cookie(sessionCookie).post(Entity.entity(CUSTOMER_UPDATE_BAD,MediaType.APPLICATION_JSON),Response.class);

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.FORBIDDEN.getStatusCode(), response.getStatus());

    response.close();
  }

  
  @Test
  public void test06_bookFlight() throws InterruptedException, ParseException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + BOOKFLIGHT_ENDPOINT; 
    
    WebTarget target = client.target(url);

    Form form = new Form();
    form.param("userid",USERNAME);
    form.param("fromAirport", "CDG");
    form.param("toAirport", "LHR");
    form.param("oneWayFlight", "true");
    form.param("fromDate", date);
    form.param("returnDate", date);
     
    Response response = target.request().cookie(sessionCookie).post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.OK.getStatusCode(), response.getStatus());

    
    String result = response.readEntity(String.class);
    assertThat(result, containsString(BOOKFLIGHT_RESPONSE));
    number = result.substring(result.indexOf("departBookingId") + 18,result.lastIndexOf("}") -1);
   
    response.close();
  }

  @Test
  public void test06_bookFlightBad() throws InterruptedException, ParseException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + BOOKFLIGHT_ENDPOINT; 
    
    WebTarget target = client.target(url);

    Form form = new Form();
    form.param("userid",USERNAME);
    form.param("fromAirport", "CDG");
    form.param("toAirport", "LHR");
    form.param("oneWayFlight", "true");
    form.param("fromDate", date);
    form.param("returnDate", date);
     
    Response response = target.request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.FORBIDDEN.getStatusCode(), response.getStatus());
   
    response.close();
  }

  @Test
  public void test07_countBookings() {
    String url = BASE_URL_WITH_CONTEXT_ROOT + COUNT_BOOKINGS_ENDPOINT;
    doTest(url, Status.OK, COUNT_BOOKINGS_RESPONSE_1);
  }

  @Test
  public void test08_getBookings() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + GET_BOOKINGS_ENDPOINT + "/" + USERNAME;

    WebTarget target = client.target(url);

    Response response = target.request().cookie(sessionCookie).get();

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, Status.OK.getStatusCode(), response.getStatus());

    String result = response.readEntity(String.class);
    assertThat(result, containsString(number));

    response.close();
  }

  @Test
  public void test08_getBookingsBad() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + GET_BOOKINGS_ENDPOINT + "/" + USERNAME;

    WebTarget target = client.target(url);

    Response response = target.request().get();

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, Status.FORBIDDEN.getStatusCode(), response.getStatus());

    response.close();
  }

  @Test
  public void test09_cancelBooking() throws InterruptedException, ParseException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + CANCELFLIGHT_ENDPOINT; 
    
    WebTarget target = client.target(url);

    Form form = new Form();
    form.param("userid",USERNAME);
    form.param("number",number);
     
    Response response = target.request().cookie(sessionCookie).post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.OK.getStatusCode(), response.getStatus());

    
    String result = response.readEntity(String.class);
    assertThat(result, containsString(CANCELFLIGHT_RESPONSE));
    
    response.close();
  }

  @Test
  public void test09_cancelBookingBad() throws InterruptedException, ParseException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + CANCELFLIGHT_ENDPOINT; 
    
    WebTarget target = client.target(url);

    Form form = new Form();
    form.param("userid",USERNAME);
    form.param("number",number);
     
    Response response = target.request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.FORBIDDEN.getStatusCode(), response.getStatus());
    
    response.close();
  }

  @Test
  public void test10_countBookings() {
    String url = BASE_URL_WITH_CONTEXT_ROOT + COUNT_BOOKINGS_ENDPOINT;
    doTest(url, Status.OK, COUNT_BOOKINGS_RESPONSE_0);
  }

  @Test
  public void test11_LogoutBad() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + LOGOUT_ENDPOINT + "?login=" + USERNAME; 
    
    WebTarget target = client.target(url);
     
    Response response = target.request().get();

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.FORBIDDEN.getStatusCode(), response.getStatus());
    
    response.close();
  }

  @Test
  public void test11_Logout() throws InterruptedException {
    String url = BASE_URL_WITH_CONTEXT_ROOT + LOGOUT_ENDPOINT + "?login=" + USERNAME; 
    
    WebTarget target = client.target(url);
     
    Response response = target.request().cookie(sessionCookie).get();

    Thread.sleep(20);
    assertEquals("Incorrect response code from " + url, 
      Status.OK.getStatusCode(), response.getStatus());

    
    String result = response.readEntity(String.class);
    assertThat(result, containsString(LOGOUT_RESPONSE));
    
    response.close();
  }

  @Test   
  public void test12_countSessions() {
    String url = BASE_URL_WITH_CONTEXT_ROOT + COUNT_SESSIONS_ENDPOINT; 
    doTest(url,Status.OK,COUNT_SESSIONS_RESPONSE_0);
  }
  
  private void doTest(String url, Status status, String expectedResponse) {
    WebTarget target = client.target(url);
    Response response = target.request().get();

    assertEquals("Incorrect response code from " + url, 
        status.getStatusCode(), response.getStatus());

    if (expectedResponse != null) {
      String result = response.readEntity(String.class);
      assertThat(result, containsString(expectedResponse));
    }
    
    response.close();
  }

      
}
