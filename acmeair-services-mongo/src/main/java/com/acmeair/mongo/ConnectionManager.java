package com.acmeair.mongo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
//import com.mongodb.async.client.*;

public class ConnectionManager implements MongoConstants{

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
	
	
	private ConnectionManager (){

		// Set default client options, and then check if there is a properties file.
		String hostname = "localhost";
		int port = 27017;
		String dbname = "acmeair";
		String username = null;
		String password = null;

		Properties prop = new Properties();
		String acmeairProps = System.getenv("ACMEAIR_PROPERTIES");
		try {
			if(acmeairProps != null){
				prop.load(new FileInputStream(acmeairProps));			
			}else {
				prop.load(ConnectionManager.class.getResourceAsStream("/config.properties"));
				acmeairProps = "OK";
			}
		}catch (IOException ex){
			logger.info("Properties file does not exist" + ex.getMessage());
			acmeairProps = null;
		}
		
		ServerAddress dbAddress = null;
		MongoClientOptions.Builder options = new MongoClientOptions.Builder();
		if(acmeairProps != null){
			try {
				logger.info("Reading mongo.properties file");
				if (System.getenv("MONGO_HOST") != null) {
					hostname = System.getenv("MONGO_HOST");
				} else if (prop.containsKey("hostname")){
					hostname = prop.getProperty("hostname");
				}
				if (System.getenv("MONGO_PORT") != null) {
					port=Integer.parseInt(System.getenv("MONGO_PORT"));
				} else if (prop.containsKey("port")){
					port = Integer.parseInt(prop.getProperty("port"));
				}
				if (System.getenv("MONGO_DBNAME") != null) {
					dbname = System.getenv("MONGO_DBNAME");
				}
				if (prop.containsKey("dbname")){
					dbname = prop.getProperty("dbname");
				}
				if (prop.containsKey("username")){
					username = prop.getProperty("username");
				}
				if (prop.containsKey("password")){
					password = prop.getProperty("password");
				}
				if (prop.containsKey("connectionsPerHost")){
					options.connectionsPerHost(Integer.parseInt(prop.getProperty("connectionsPerHost")));
				}
				if (prop.containsKey("minConnectionsPerHost")){
					options.minConnectionsPerHost(Integer.parseInt(prop.getProperty("minConnectionsPerHost")));
				}
				if (prop.containsKey("maxWaitTime")){
					options.maxWaitTime(Integer.parseInt(prop.getProperty("maxWaitTime")));
				}
				if (prop.containsKey("connectTimeout")){
					options.connectTimeout(Integer.parseInt(prop.getProperty("connectTimeout")));
				}
				if (prop.containsKey("socketTimeout")){
					options.socketTimeout(Integer.parseInt(prop.getProperty("socketTimeout")));
				}
				if (prop.containsKey("socketKeepAlive")){
					options.socketKeepAlive(Boolean.parseBoolean(prop.getProperty("socketKeepAlive")));
				}
				if (prop.containsKey("sslEnabled")){
					options.sslEnabled(Boolean.parseBoolean(prop.getProperty("sslEnabled")));
				}
				if (prop.containsKey("threadsAllowedToBlockForConnectionMultiplier")){
					options.threadsAllowedToBlockForConnectionMultiplier(Integer.parseInt(prop.getProperty("threadsAllowedToBlockForConnectionMultiplier")));
				}
				
			}catch (Exception ioe){
				logger.severe("Exception when trying to read from the mongo.properties file" + ioe.getMessage());
			}
		}

		MongoClientOptions builtOptions = options.build();
		
		try {
			//Check if VCAP_SERVICES exist, and if it does, look up the url from the credentials.
			String vcapJSONString = System.getenv("VCAP_SERVICES");
			if (vcapJSONString != null) {
				logger.info("Reading VCAP_SERVICES");
				Object jsonObject = JSONValue.parse(vcapJSONString);
				JSONObject vcapServices = (JSONObject)jsonObject;
				JSONArray mongoServiceArray =null;					
				for (Object key : vcapServices.keySet()){
					if (key.toString().startsWith("mongo")){
						mongoServiceArray = (JSONArray) vcapServices.get(key);
						logger.info("Service Type : MongoLAB - " + key.toString());
						break;
					}
					if (key.toString().startsWith("user-provided")){
						mongoServiceArray = (JSONArray) vcapServices.get(key);
						logger.info("Service Type : MongoDB by Compost - " + key.toString());
						break;
					}
				}
				
				if (mongoServiceArray == null) {
					logger.info("VCAP_SERVICES existed, but a MongoLAB or MongoDB by COMPOST service was not definied. Trying DB resource");
					//VCAP_SERVICES don't exist, so use the DB resource
					dbAddress = new ServerAddress (hostname, port);

					// If username & password exists, connect DB with username & password
					if ((username == null)||(password == null)){
						mongoClient = new MongoClient(dbAddress, builtOptions);
					}else {
						List<MongoCredential> credentials = new ArrayList<>();
						credentials.add(MongoCredential.createCredential(username, dbname, password.toCharArray()));
						mongoClient = new MongoClient(dbAddress,credentials, builtOptions);
					}
				} else {					
					JSONObject mongoService = (JSONObject)mongoServiceArray.get(0); 
					JSONObject credentials = (JSONObject)mongoService.get("credentials");
					String url = (String) credentials.get("url");
					logger.fine("service url = " + url);				
					MongoClientURI mongoURI = new MongoClientURI(url, options);
					mongoClient = new MongoClient(mongoURI);
					dbname = mongoURI.getDatabase();
					
				}
			}else {
				
				//VCAP_SERVICES don't exist, so use the DB resource
				dbAddress = new ServerAddress (hostname, port);

				// If username & password exists, connect DB with username & password
				if ((username == null)||(password == null)){
					mongoClient = new MongoClient(dbAddress, builtOptions);
				}else {
					List<MongoCredential> credentials = new ArrayList<>();
					credentials.add(MongoCredential.createCredential(username, dbname, password.toCharArray()));
					mongoClient = new MongoClient(dbAddress,credentials, builtOptions);
				}
			}
			
			db = mongoClient.getDatabase(dbname);
			logger.info("#### Mongo DB Server " + mongoClient.getAddress().getHost() + " ####");
			logger.info("#### Mongo DB Port " + mongoClient.getAddress().getPort() + " ####");
			logger.info("#### Mongo DB is created with DB name " + dbname + " ####");
			logger.info("#### MongoClient Options ####");
			logger.info("maxConnectionsPerHost : "+ builtOptions.getConnectionsPerHost());
			logger.info("minConnectionsPerHost : "+ builtOptions.getMinConnectionsPerHost());
			logger.info("maxWaitTime : "+ builtOptions.getMaxWaitTime());
			logger.info("connectTimeout : "+ builtOptions.getConnectTimeout());
			logger.info("socketTimeout : "+ builtOptions.getSocketTimeout());
			logger.info("socketKeepAlive : "+ builtOptions.isSocketKeepAlive());
			logger.info("sslEnabled : "+ builtOptions.isSslEnabled());
			logger.info("threadsAllowedToBlockForConnectionMultiplier : "+ builtOptions.getThreadsAllowedToBlockForConnectionMultiplier());
			logger.info("Complete List : "+ builtOptions.toString());

		}catch (Exception e) {
			logger.severe("Caught Exception : " + e.getMessage() );				
		}
		

	}
	
	public MongoDatabase getDB(){
		return db;
	}
}
