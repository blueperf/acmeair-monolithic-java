# Acme Air Sample and Benchmark (wasperf version)

This application shows an implementation of a fictitious airline called "Acme Air".  The application was built with the some key business requirements: the ability to scale to billions of web API calls per day, the need to develop and deploy the application in public clouds (as opposed to dedicated pre-allocated infrastructure), and the need to support multiple channels for user interaction (with mobile enablement first and browser/Web 2.0 second).

This version of acmeair supports:
  - WebSphere Liberty Profile to Mongodb

## Repository Contents

Source:

- **acmeair-services**:  The Java data services interface definitions
- **acmeair-service-mongo**:  A WebSphere eXtreme Scale data service implementation
- **acmeair-webapp**:  The Web 2.0 application and associated Java REST services (monolithic or break out into micro-services below)
- **acmeair-as**:  The Authentication Java Rest MicroService application
- **acmeair-cs**:  The Customer Java Rest MicroService application
- **acmeair-fbs**:  The Flight Booking Java Rest MicroService application

## How to get started

* Instructions for [setting up and building the codebase](Documentation/Build_Instructions.md)
* Deploying the sample application to [Websphere Liberty](Documentation/Liberty_Instructions.md)
* Deploying to [IBM Bluemix](Documentation/Bluemix_Instructions.md)
* Acme Air for Node.js [Instructions](https://github.com/wasperf/acmeair-nodejs/blob/master/README.md)


