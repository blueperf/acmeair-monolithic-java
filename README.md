# Acme Air Sample and Benchmark (monolithic simple version)

This application shows an implementation of a fictitious airline called "Acme Air".  The application was built with some key business requirements: the ability to scale to billions of web API calls per day, the need to develop and deploy the application targeting multiple cloud platforms (including Public, Dedicated, Private and hybrid) and the need to support multiple channels for user interaction (with mobile enablement first and browser/Web 2.0 second).The application can be deployed both on-prem as well as on Cloud platforms. 

This version of acmeair supports:
  - WebSphere Liberty Profile to Mongodb

#Setup
Use maven to build the project
 - git clone https://github.com/blueperf/acmeair -b simple --single-branch
 - cd acmeair
 - mvn clean package
 
#For CF
 - mkdir apps
 - cp target/acmeair-java-2.0.0-SNAPSHOT.war apps
 - bx cf push acme-java-myname -p ../acmeair -m 512M
  
#For Container Services
 - docker build -f ./Docker_CS -t registry.**REGION**.bluemix.net/**NAMESPACE**/IMAGENAME .
 - docker push registry.**REGION**.bluemix.net/**NAMESPACE**/IMAGENAME
 - Modify acmeairJAVA.yaml to add registry.**REGION**.bluemix.net/**NAMESPACE**/IMAGENAME as the image name
 - kubectl create -f ./acmeairJAVA.yaml
