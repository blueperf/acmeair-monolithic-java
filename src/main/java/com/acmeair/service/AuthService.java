/*******************************************************************************
* Copyright (c) 2013-2015 IBM Corp.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
package com.acmeair.service;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class AuthService {
	protected static final int DAYS_TO_ALLOW_SESSION = 1;
	
	@Inject
	protected KeyGenerator keyGenerator;
	
	
		
	// TODO: Do I really need to create a JSONObject here or just return a Json string?
	public JSONObject validateSession(String sessionid) {
		String cSession = getSession(sessionid);
		if (cSession == null) {
			return null;
		}

		try{
			Date now = new Date();
			JSONObject sessionJson = (JSONObject) new JSONParser().parse(cSession);
			String timeoutString = sessionJson.get("timeoutTime").toString();
			JSONObject timeJson = (JSONObject) new JSONParser().parse(timeoutString);
			
			if (now.getTime() > (Long)timeJson.get("$date")) {
				removeSession(cSession);
				return null;
			}
			
			return sessionJson;
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}		
	}
	
	protected abstract String getSession(String sessionid);
	
	protected abstract void removeSession(String sessionJson);
	
	// TODO: Do I really need to create a JSONObject here or just return a Json string?
	// TODO: Maybe simplify as Moss did, but need to change node.js version first
	public JSONObject createSession(String customerId) {
		String sessionId = keyGenerator.generate().toString();
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		c.add(Calendar.DAY_OF_YEAR, DAYS_TO_ALLOW_SESSION);
		Date expiration = c.getTime();
		
		JSONObject sessionJson = null;
		
		try{
			sessionJson = (JSONObject) new JSONParser().parse(createSession(sessionId, customerId, now, expiration));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
		return sessionJson;
	}
	
	protected abstract String createSession(String sessionId, String customerId, Date creation, Date expiration);

	public abstract void invalidateSession(String sessionid);
	
	public abstract Long countSessions();

	public abstract void dropSessions(); 

}
