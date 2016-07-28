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

import javax.inject.Inject;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.acmeair.web.dto.CustomerInfo;

public abstract class CustomerService {
	protected static final int DAYS_TO_ALLOW_SESSION = 1;
	
	@Inject
	protected KeyGenerator keyGenerator;
	
	public abstract void createCustomer(
			String username, String password, String status, int total_miles,
			int miles_ytd, String phoneNumber, String phoneNumberType, String addressJson);
	
	public abstract String createAddress (String streetAddress1, String streetAddress2,
			String city, String stateProvince, String country, String postalCode);
	
	public abstract void updateCustomer(String username,  CustomerInfo customerJson);
		
	
	protected abstract String getCustomer(String username);
	
	public abstract String getCustomerByUsername(String username);
	
	public boolean validateCustomer(String username, String password) {
		boolean validatedCustomer = false;
		String customerToValidate = getCustomer(username);
		if (customerToValidate != null) {
			try{
				JSONObject customerJson = (JSONObject) new JSONParser().parse(customerToValidate);
				validatedCustomer = password.equals((String)customerJson.get("password"));
			}catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return validatedCustomer;
	}
	
	public String getCustomerByUsernameAndPassword(String username, String password) {
		String c = getCustomer(username);
		try{
			JSONObject customerJson = (JSONObject) new JSONParser().parse(c);
			if (!customerJson.get("password").equals(password)) {
				return null;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		// Should we also set the password to null?
		return c;
	}
			
	public abstract Long count();

	public abstract void dropCustomers();
		
}
