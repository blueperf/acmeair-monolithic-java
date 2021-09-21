package com.acmeair.mongo;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.mongodb.connection.ConnectionPoolSettings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.acmeair.mongo.MongoConstants;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class ConnectionManager implements MongoConstants {

	private static AtomicReference<ConnectionManager> connectionManager = new AtomicReference<ConnectionManager>();

	private final static Logger logger = Logger.getLogger(ConnectionManager.class.getName());

	protected MongoClient mongoClient;
	protected MongoDatabase db;

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

		// Set default client options, and then check if there is a properties
		// file.
		String hostname = "localhost";
		int port = 27017;
		String dbname = "acmeair";
		int maxConnections = 100;	// default is 100
		int minConnections = 10; 	// default is 10
		int connectionTimeout = 10;	// default is 10

		String mongoManual = System.getenv("MONGO_MANUAL");
		Boolean isManual = Boolean.parseBoolean(mongoManual);

		String mongoMaxConnectionsPerHost = System.getenv("MONGO_MAX_CONNECTIONS");
		if (mongoMaxConnectionsPerHost != null) {
			maxConnections = Integer.parseInt(mongoMaxConnectionsPerHost);
		}

		String mongoMinConnectionsPerHost = System.getenv("MONGO_MIN_CONNECTIONS");
		if (mongoMinConnectionsPerHost != null) {
			minConnections = Integer.parseInt(mongoMinConnectionsPerHost);
		}

		String mongoConnectionTimeout = System.getenv("MONGO_CONNECTION_TIMEOUT");
		if (mongoConnectionTimeout != null) {
			connectionTimeout = Integer.parseInt(mongoConnectionTimeout);
		}

		String mongoHost = System.getenv("MONGO_HOST");
		if (mongoHost != null) {
			hostname = mongoHost;
		}

		String mongoPort = System.getenv("MONGO_PORT");
		if (mongoPort != null) {
			port = Integer.parseInt(mongoPort);
		}

		String mongoDbName = System.getenv("MONGO_DBNAME");
		if (mongoDbName != null) {
			dbname = mongoDbName;
		}

		String mongoUser = System.getenv("MONGO_USER");

		String mongoPassword = System.getenv("MONGO_PASSWORD");
		MongoClientOptions.Builder clientOptions = new MongoClientOptions.Builder();
		clientOptions.connectionsPerHost(maxConnections)
				.minConnectionsPerHost(minConnections)
				.connectTimeout(connectionTimeout);

		MongoClientOptions options = clientOptions.build();

		try {

			// If MONGO_MANUAL is set to true, it will set up the DB connection
			// right away
			if (isManual) {
				if (mongoUser != null) {
					MongoCredential credential = MongoCredential.createCredential(mongoUser, dbname,
							mongoPassword.toCharArray());
					mongoClient = new MongoClient(new ServerAddress(hostname, port),Arrays.asList(credential),options);
				}else {
					mongoClient = new MongoClient(new ServerAddress(hostname, port), options);
				}
			} else {
				// Check if VCAP_SERVICES exist, and if it does, look up the url
				// from the credentials.
				String vcapJSONString = System.getenv("VCAP_SERVICES");
				if (vcapJSONString != null) {
					logger.info("Reading VCAP_SERVICES");
					Object jsonObject = JSONValue.parse(vcapJSONString);
					JSONObject vcapServices = (JSONObject) jsonObject;
					JSONArray mongoServiceArray = null;
					for (Object key : vcapServices.keySet()) {
						if (key.toString().startsWith("user-provided")) {
							mongoServiceArray = (JSONArray) vcapServices.get(key);
							logger.info("Service Type : MongoDB by Compost - " + key.toString());
							break;
						}
					}
					JSONObject mongoService = (JSONObject) mongoServiceArray.get(0);
					JSONObject credentials = (JSONObject) mongoService.get("credentials");
					String url = (String) credentials.get("url");
					logger.fine("service url = " + url);
					MongoClientURI mongoURI = new MongoClientURI(url, clientOptions);
					mongoClient = new MongoClient(mongoURI);
					dbname = mongoURI.getDatabase();

				}else {
					mongoClient = new MongoClient(new ServerAddress(hostname, port), options);
				}
			}
			logger.fine("#### Mongo DB Database Name " + dbname + " ####");
			db = mongoClient.getDatabase(dbname);
			logger.info("#### Mongo DB Server " + mongoClient.getAddress().getHost() + " ####");
			logger.info("#### Mongo DB Port " + mongoClient.getAddress().getPort() + " ####");
			logger.info("#### Mongo DB is created with DB name " + dbname + " ####");
			logger.info("#### Mongo Max Conn " + maxConnections + " ####");
			logger.info("### Mongo Configuration:");
			logger.info(options.toString());
		} catch (Exception e) {
			logger.severe("Caught Exception : " + e.getMessage());
		}

	}

	public MongoDatabase getDB() {
		return db;
	}
}