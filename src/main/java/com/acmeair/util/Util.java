package com.acmeair.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Util {

	
	public static void registerService(){

		String PORT = System.getenv("VCAP_APP_PORT");
		String NAME = System.getenv("SERVICE_NAME");
		String BEARER_TOKEN = System.getenv("SD_TOKEN");
		String SD_URL = System.getenv("SD_URL");
		String space_id = System.getenv("space_id");

		int TIME_TO_LIVE = 300;
		int SLEEP_TIME= new Double(TIME_TO_LIVE*0.9*1000).intValue();
		
		String requestUrl = SD_URL + "/api/v1/instances";

		if (space_id != null){
			String SERVICE_IP = "";
			try {
				SERVICE_IP = Inet4Address.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JSONObject jsonObj = new JSONObject();
			JSONObject endpoint = new JSONObject();
			JSONArray empty = new JSONArray();
			
			endpoint.put("type", "http");
			endpoint.put("value", SERVICE_IP +":"+ PORT);
			
			jsonObj.put("tags",empty);
			jsonObj.put("status","UP");
			jsonObj.put("service_name", NAME);
			jsonObj.put("endpoint", endpoint);
			jsonObj.put("ttl", TIME_TO_LIVE);
			
			byte[] postData = jsonObj.toJSONString().getBytes( StandardCharsets.UTF_8 );

			URL url;
			while (true){
				try {
					System.out.println("REGISTERING THIS SERVICE...");	
					url = new URL( requestUrl );
					HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
					conn.setDoOutput( true );
					conn.setInstanceFollowRedirects( false );
					conn.setRequestMethod( "POST" );
					conn.setRequestProperty( "Content-Type", "application/json"); 
					conn.setRequestProperty( "authorization", "Bearer " + BEARER_TOKEN);
					conn.setRequestProperty( "X-Forwarded-Proto", "https");
					try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
					   wr.write( postData );
					   wr.flush();
					   wr.close();
					}
					
					String line;
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					StringBuffer response = new StringBuffer();
					while((line = br.readLine()) != null) {
						response.append(line);
					}
					br.close();

					System.out.println("Response Code : " + conn.getResponseCode() 
							+ " Response : " + response.toString());
					JSONParser parser = new JSONParser();
					JSONObject responseJson = (JSONObject)parser.parse(response.toString());
					JSONObject linkJson = (JSONObject)parser.parse(responseJson.get("links").toString());
					try{
						sendHeartbeat((String)linkJson.get("heartbeat"), BEARER_TOKEN, SLEEP_TIME);
					}catch (Exception e){
						System.out.println("HEARTBEAT FAILED AT " + e.getClass() + " WITH ERROR : " + e.getMessage() + " RE-REGISTERING");
					}
				} catch (Exception e) {
					int sleepTime = 10000;
					System.out.println("REGISTRATION FAILED AT " + e.getClass() + " WITH ERROR : " + e.getMessage() + " RE-REGISTERING AFTER " + sleepTime/1000 + " sec  SLEEP");	
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}				
		}
	}
	
	public static void sendHeartbeat(String heartbeatUrl, String BEARER_TOKEN, int SLEEP_TIME) throws Exception{
		URL url;
		while (true){
			Thread.sleep(SLEEP_TIME);
			System.out.print("Heartbeat Check ");
			url = new URL( heartbeatUrl );
			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
			conn.setDoOutput( true );
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "PUT" );
			conn.setRequestProperty( "Content-Type", "application/json"); 
			conn.setRequestProperty( "authorization", "Bearer " + BEARER_TOKEN);
			conn.setRequestProperty( "X-Forwarded-Proto", "https");
			conn.getOutputStream();
			int responseCode = conn.getResponseCode();
			System.out.println( "Response Code : " + responseCode);
			if (responseCode != 200){
				throw new Not200Exception(Integer.toString(conn.getResponseCode()));
			}
		}
	}
	
	static class Not200Exception extends Exception
	{
		private static final long serialVersionUID = 1L;

		public Not200Exception(String message)
		{
			super(message);
		}
	}
	
	public static String getServiceProxy() {

		String SD_URL = System.getenv("SD_URL");
		String BEARER_TOKEN = System.getenv("SD_TOKEN");

		String requestUrl = SD_URL + "/api/v1/services/ServiceProxy";

		while (true){
			try {
				
				System.out.println("GETTING SERVICE PROXY URL...");
				URL url = new URL( requestUrl );
				HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
				conn.setRequestProperty( "authorization", "Bearer " + BEARER_TOKEN);
				
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer response = new StringBuffer();
				while((line = br.readLine()) != null) {
					response.append(line);
				}
				br.close();

				System.out.println("SP Response Code : " + conn.getResponseCode() 
						+ " SP Response : " + response.toString());
				
				JSONParser parser = new JSONParser();
				JSONObject responseJson = (JSONObject)parser.parse(response.toString());
				JSONArray instancesJson = (JSONArray)parser.parse(responseJson.get("instances").toString());
				JSONObject instanceJson = (JSONObject)parser.parse(instancesJson.get(0).toString());
				JSONObject endpointJson = (JSONObject)parser.parse(instanceJson.get("endpoint").toString());
				return (String) endpointJson.get("value");
			} catch (Exception e) {
				int sleepTime = 10000;
				System.out.println("FAILED TO GET THE SERVICE PROXY AT " + e.getClass() + " WITH ERROR : " + e.getMessage() + " RE-TRYING AFTER " + sleepTime/1000 + " sec  SLEEP");	
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}
