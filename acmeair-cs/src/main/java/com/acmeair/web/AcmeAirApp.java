package com.acmeair.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.acmeair.config.AcmeAirConfiguration;
import com.acmeair.config.LoaderREST;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/rest/api")
public class AcmeAirApp extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(CustomerREST.class,AcmeAirConfiguration.class, LoaderREST.class));
    }
}
