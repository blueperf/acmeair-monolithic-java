package com.acmeair.mongo.services;

import static com.mongodb.client.model.Filters.eq;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.bson.Document;


import com.acmeair.mongo.ConnectionManager;
import com.acmeair.mongo.MongoConstants;
import com.acmeair.service.AuthService;
import com.acmeair.service.DataService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;



@DataService(name=MongoConstants.KEY,description=MongoConstants.KEY_DESCRIPTION)
public class AuthServiceImpl extends AuthService implements MongoConstants {	
		
//	private final static Logger logger = Logger.getLogger(CustomerService.class.getName()); 
	
	private MongoCollection<Document> customerSession;
	
	@PostConstruct
	public void initialization() {	
		MongoDatabase database = ConnectionManager.getConnectionManager().getDB();
		customerSession = database.getCollection("customerSession");
	}
	
	@Override
	public Long countSessions() {
		return customerSession.count();
	}
	
	@Override
	protected String getSession(String sessionid){
		return customerSession.find(eq("_id", sessionid)).first().toJson();
	}
	
	@Override
	protected void removeSession(String sessionJson){
		new Document();
		customerSession.deleteMany(Document.parse(sessionJson));
	}
	
	@Override
	protected  String createSession(String sessionId, String customerId, Date creation, Date expiration) {
		Document sessionDoc = new Document("_id", sessionId)
        .append("customerid", customerId)
        .append("lastAccessedTime", creation)
        .append("timeoutTime", expiration);
		
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
