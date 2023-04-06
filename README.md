### Acme Air Monolith Sample and Benchmark for MongoDB

This application shows an implementation of a fictitious airline called "Acme Air" to exercise OpenLiberty JEE Profile with MongoDB as a datastore.

The application is forked from https://github.com/blueperf/acmeair-monolithic-java

The application depends on: 

> mongodb | 4.4.18 | docker pull icr.io/ppc64le-oss/mongodb-ppc64le:4.4.18

### Setup/Build
Use maven to build the project
 - git clone https://github.com/prb112/acmeair-monolithic-java
 - cd acmeair-monolithic-java
 - mvn clean package

- Setup Maven `brew install maven`
 
  **Other Setups - Setup DB**
 - First, create a Compost account, then create a Apache Cassandra DB Deployment (It is a paid service with 30 days free trial)
 - Create a database with the name "acmeair"
 - get these information:
   - "hostname
   - "port"
   - "db"
   - "username"
   - "password"

acmeair-monolithic-java on  main [✘!?] 
❯ export MONGODB_USER=$(echo admin | base64) 

acmeair-monolithic-java on  main [✘!?] 
❯ export MONGODDB_PASS=$(echo pass1234 | base64)

# Database loading
 - Go to the home page http://hostname:port
 - At the bottom of the page, click the link : Configure the Acme Air Environment > Click **Load the database**
 
# Driving the load
 - Follow the instruction [here](https://github.com/blueperf/acmeair-driver)
 - Use performance/acmeair.jmx 
 - jmeter -n -t performance/acmeair.jmx -DusePureIDs=true -JHOST=hostname -JPORT=80 -j logName -JTHREAD=1 -JUSER=999 -JDURATION=60 -JRAMP=0 ;

The code may not use https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/connection/connect/

Thanks for DevOpsCube for https://devopscube.com/deploy-mongodb-kubernetes/