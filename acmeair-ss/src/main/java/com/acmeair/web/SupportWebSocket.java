/**
 * (C) Copyright IBM Corporation 2016.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acmeair.web;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/supportrequest")
public class SupportWebSocket {

	private Session currentSession = null;
	private String agent = null;  
	
    @OnOpen
    public void onOpen(final Session session, EndpointConfig ec) {
    	currentSession = session;
    	agent = getRandomSupportAgent(); 
    	
    	String greeting = getGreeting(agent); 
    	currentSession.getAsyncRemote().sendText(greeting);
    } 
    
    @OnMessage
    public void sendResponse(String message) {
    	
    	String toSend = getRandomSupportResponse(agent);
    	currentSession.getAsyncRemote().sendText(toSend); 
    }

    @OnError
    public void onError(Throwable t) {
        
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {      

    }
    
    // TODO: JSONify this
    
    private int getRandomInt(int min, int max) {
    	return (int) (Math.floor(Math.random() * (max - min)) + min);
    }
	
    private String getRandomSupportAgent() {
    	String[] supportStaffNames = {"Bob","Bill","Beth","Don","Jim","Kate","Matt","Mike","Nick","Rick","Steve","Tina","Tom"};
		return supportStaffNames[getRandomInt(0,supportStaffNames.length)];
    }
   
    private String getGreeting(String agentName) {
    	return "{\"agent\" : \"" + agentName + "\",\"message\" : \"Welcome to AcmeAir Support. My name is " 
    			+ agentName + ". How can I help you?\"}";
    }
   
    private String getRandomSupportResponse(String agentName) {
    	String[] supportResponses = {"One moment please...", 
    			"Did you try rebooting?", 
    			"Is it plugged in?", 
    			"Let me contact L2."};
				
    	return "{\"agent\" : \"" + agentName + "\",\"message\" : \"" + 
    		supportResponses[getRandomInt(0,supportResponses.length)] + "\"}";
	}  
}