FROM websphere-liberty:common

ADD Dockerfile /root/Dockerfile

RUN rm -rf /opt/ibm/wlp/usr/servers/defaultServer/server.xml
ADD server.xml /opt/ibm/wlp/usr/servers/defaultServer/server.xml

RUN installUtility install --acceptLicense defaultServer
RUN rm -rf /opt/ibm/wlp/usr/servers/defaultServer/workarea

ADD ./build/libs/*.war /opt/ibm/wlp/usr/servers/defaultServer/apps

EXPOSE 80

ENV CUSTOMER_SERVICE=nginx1/acmeair
ENV MONGO_HOST=session_db1
ENV MONGO_DBNAME=acmeair_sessiondb

CMD ["/opt/ibm/wlp/bin/server", "run", "defaultServer"]
