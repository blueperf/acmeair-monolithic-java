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
package com.acmeair.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.acmeair.AcmeAirConstants;

public class Loader implements AcmeAirConstants{

	private Properties props;
	String numCustomers;
	int maxDaysToScheduleFlights;

	Loader(){
		props = new Properties();
		String acmeairProps = System.getenv("ACMEAIR_PROPERTIES");

		//Set the MAX DAYS TO SCHEDULE FLIGHTS. Default is 30
		if(acmeairProps != null){
			try {
				logger.info("Reading ACMEAIR_PROPERTIES");
				props.load(new FileInputStream(acmeairProps));
				numCustomers = props.getProperty("loader.numCustomers","100");
				maxDaysToScheduleFlights = Integer.parseInt(props.getProperty("loader.maxDaysToScheduleFlights","5"));
				}catch (IOException ioe){
					logger.severe("Exception when trying to read from the mongo.properties file" + ioe.getMessage());
				}
		}
	}
	
	public String queryLoader() {			
		return numCustomers;	
	}
	
	public String loadDB(long numCustomers) {
		FlightLoader flightLoader = new FlightLoader();
		CustomerLoader customerLoader = new CustomerLoader();

    	double length = 0;
    	//Call flightLoader.loadFlights FIRST so that it will drop the DB to reinitialize
		try {
			long start = System.currentTimeMillis();
			logger.info("Start loading flights");
			flightLoader.loadFlights(maxDaysToScheduleFlights);
			logger.info("Start loading " +  numCustomers + " customers");
			customerLoader.loadCustomers(numCustomers);
			long stop = System.currentTimeMillis();
			logger.info("Finished loading in " + (stop - start)/1000.0 + " seconds");
			length = (stop - start)/1000.0;
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
		return "Loaded flights and "  +  numCustomers + " customers in " + length + " seconds";
	}
}