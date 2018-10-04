# Acme Air Sample and Benchmark (monolithic simple version)

This application shows an implementation of a fictitious airline called "Acme Air".  The application was built with some key business requirements: the ability to scale to billions of web API calls per day, the need to develop and deploy the application targeting multiple cloud platforms (including Public, Private and hybrid).  The application can be deployed both on-prem as well as on Cloud platforms. 

This version of acmeair supports:
  - WebSphere Liberty Profile to Mongodb

# Setup
Use maven to build the project
 - git clone https://github.com/blueperf/acmeair-monolithic-java
 - cd acmeair-monolithic-java
 - mvn clean package
 
  **Setup DB**
 - First, create a Compost account, then create a Mongo DB Deployment (It is a paid service with 30 days free trial)
 - Create a database with the name "acmeair"
 - get these information:
   - "hostname
   - "port"
   - "db"
   - "username"
   - "password"
 
# For CF
 - mkdir apps
 - cp target/acmeair-java-2.0.0-SNAPSHOT.war apps
 - ibmcloud cf push acme-java-myname -p ../acmeair -m 512M
 
Add these environment variables and restage
   - MONGO_MANUAL : true
   - MONGO_HOST : <hostname>
   - MONGO_PORT : <port>
   - MONGO_DBNAME : <db>
   - MONGO_USER : <username>
   - MONGO_PASSWORD : <password>

(Alternative) 
**Create user provided DB Service**
- Create a string using Compose database information:
   - "url": "mongodb://username:password@hostname:port/db"
   - e.g. mongodb://acmeuser:password@myServer.dblayer.com:27017/acmeair
 
- Use CF command to create DB:
   - cf cups mongodbCompose -p "url"
   - At the URL prompt, enter above URL that was created:
   - url>mongodb://acmeuser:password@myServer.dblayer.com:27017/acmeair
 
- On IBM Cloud Dasboard, bind the created mongodbCompose service to Acmeair
   - restage/restart Acmeair application 
  
# For Kubernetes Services
 - docker build -f ./Dockerfile_KS -t registry.**REGION**.bluemix.net/**NAMESPACE**/IMAGENAME .
 - docker push registry.**REGION**.bluemix.net/**NAMESPACE**/IMAGENAME
 - Modify acmeair-monolithic-java.yaml to add registry.**REGION**.bluemix.net/**NAMESPACE**/IMAGENAME as the image name
 - Modify acmeair-monolithic-java.yaml to add DB connection information (Note: If there is no user setup for this DB, REMOVE MONGO_USER & MONGO_PASSWORD entries)
 - kubectl create -f ./acmeair-monolithic-java.yaml

# Database loading
 - Go to the home page http://hostname:port
 - At the bottom of the page, click the link : Configure the Acme Air Environment > Click **Load the database**
 
# Driving the load
 - Follow the instruction [here](https://github.com/blueperf/acmeair-driver)
 - Use AcmeAir-v5.jmx insread of AcmeAir-microservices.jmx (Note: there is a limitation for JAX-RS to use root directory)
 - jmeter -n -t AcmeAir-v5.jmx -DusePureIDs=true -JHOST=hostname -JPORT=80 -j logName -JTHREAD=1 -JUSER=999 -JDURATION=60 -JRAMP=0 ;
