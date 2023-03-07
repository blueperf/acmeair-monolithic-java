FROM icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi

COPY --chown=1001:0 src/main/liberty/config/server.xml /config/server.xml
RUN features.sh

COPY --chown=1001:0 target/acmeair-monolithic-jakarta.war /config/apps/

RUN configure.sh 
