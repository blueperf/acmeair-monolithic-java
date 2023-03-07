package com.acmeair.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class ServiceRegister implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Thread registration = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	Util.registerService();  
		    }	    
		});
		registration.start();
	}
}
