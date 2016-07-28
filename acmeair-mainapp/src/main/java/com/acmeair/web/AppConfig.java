package com.acmeair.web;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;

import com.acmeair.config.AcmeAirConfiguration;


@ApplicationPath("/rest/api")
public class AppConfig extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(AcmeAirConfiguration.class));
    }
}
