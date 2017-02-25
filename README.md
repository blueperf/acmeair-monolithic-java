# Acme Air Sample and Benchmark (wasperf version)

This application shows an implementation of a fictitious airline called "Acme Air".  The application was built with some key business requirements: the ability to scale to billions of web API calls per day, the need to develop and deploy the application targeting multiple cloud platforms (including Public, Dedicated, Private and hybrid) and the need to support multiple channels for user interaction (with mobile enablement first and browser/Web 2.0 second).The application can be deployed both on-prem as well as on Cloud platforms. 

This application has been restructured to be a microservices application in addion to a monolithic application. Monolithc appliction is refactored to make sure the application can scale with multiple Cloud Data Servces. Microservices version is being developed actively to support multiple Service Proxy, Service Discovery and Resliency technologies. This application is also being enhanced with additional services using IBM Cognitive capabilites like Watson Dialog etc.,

This version of acmeair supports:
  - WebSphere Liberty Profile to Mongodb

## Repository Contents

Source:

- **acmeair-services**:  The Java data services interface definitions
- **acmeair-service-mongo**:  A WebSphere eXtreme Scale data service implementation
- **acmeair-webapp**:  The Web 2.0 application and associated Java REST services (monolithic app)
- **acmeair-mainapp**:  The Front End and Config MicroService application
- **acmeair-as**:  The Authentication Java Rest MicroService application
- **acmeair-cs**:  The Customer Java Rest MicroService application
- **acmeair-fs**:  The Flight Java Rest MicroService application
- **acmeair-bs**:  The Booking Java Rest MicroService application

## How to get started

* Instructions for [setting up and building the codebase](Documentation/Build_Instructions.md)
* Deploying the sample application to [Websphere Liberty](Documentation/Liberty_Instructions.md)
* Deploying the sample application as microservices in docker [Websphere Liberty](Documentation/Docker_Instructions.md)
* Deploying to [IBM Bluemix](Documentation/Bluemix_Instructions.md)
* Acme Air for Node.js [Instructions](https://github.com/wasperf/acmeair-nodejs/blob/master/README.md)

* NOTE: DO NOT use acmeair.properties file to configure database unless there is specific needs.  Use Service Bridge for Mongo DB to get good performance results (When using acmeair.properties file, make sure to configure every DB options properly - if only setting up the hostname, port number & credentials, it will give poor performance)



