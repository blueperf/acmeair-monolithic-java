package com.acmeair.mongo;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
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

		String mongoManual = System.getenv("MONGO_MANUAL");
		Boolean isManual = Boolean.parseBoolean(mongoManual);

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
		try {

			// If MONGO_MANUAL is set to true, it will set up the DB connection
			// right away
			if (isManual) {
				if (mongoUser != null) {
					MongoCredential credential = MongoCredential.createCredential(mongoUser, dbname,
							mongoPassword.toCharArray());
					mongoClient = new MongoClient(new ServerAddress(hostname, port),Arrays.asList(credential));
				}else {
					mongoClient = new MongoClient(hostname, port);
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
					MongoClientURI mongoURI = new MongoClientURI(url);
					mongoClient = new MongoClient(mongoURI);
					dbname = mongoURI.getDatabase();

				}else {
					mongoClient = new MongoClient(hostname, port);
				}
			}
			logger.fine("#### Mongo DB Database Name " + dbname + " ####");
			db = mongoClient.getDatabase(dbname);
			logger.info("#### Mongo DB Server " + mongoClient.getAddress().getHost() + " ####");
			logger.info("#### Mongo DB Port " + mongoClient.getAddress().getPort() + " ####");
			logger.info("#### Mongo DB is created with DB name " + dbname + " ####");
		} catch (Exception e) {
			logger.severe("Caught Exception : " + e.getMessage());
		}

	}

	public MongoDatabase getDB() {
		return db;
	}
}
