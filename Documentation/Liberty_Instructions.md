# WebSphere Liberty Setup Instructions 

If you have not already done so, read through the [instructions for building the codebase](Build_Instructions.md) first. 


## Download the following free for developers or trial versions of WebSphere software

WebSphere Liberty 8.5.5.6
* Download link:  https://developer.ibm.com/wasdev/downloads/liberty-profile-using-non-eclipse-environments
* Click on the "Download Zip" for the "WAS Liberty with Java EE 7 Web Profile" image 
* Review and agree to the license, click the "Accept and download" button and save the resulting "wlp-webProfile7-8.5.5.6.zip" file.


* Install the WebSphere Liberty Profile Developers Runtime file into a directory of your choice
```text
unzip wlp-webProfile7-8.5.5.6.zip
```

* For the rest of these instructions we will assume this to be the WLP_SERVERDIR
Windows:
```text
set WLP_SERVERDIR=C:\work\java\wlp
```
 
Linux:
```text
export WLP_SERVERDIR=~/work/java/wlp
```

# Monolithic Mode

## Create the WebSphere Liberty server and then deploy the application
Windows:
```text
cd %WLP_SERVERDIR%
bin\server create acemair
```

Linux:
```text
cd $WLP_SERVERDIR
bin/server create acmeair
```

* Copy the web application you previously built  

Windows:
```text
copy %ACMEAIR_SRCDIR%\acmeair-webapp\build\libs\acmeair-webapp-2.0.0-SNAPSHOT.war %WLP_SERVERDIR%\usr\servers\acmeair\apps\.
```

Linux:
```text
cp $ACMEAIR_SRCDIR/acmeair-webapp/build/libs/acmeair-webapp-2.0.0-SNAPSHOT.war $WLP_SERVERDIR/usr/servers/acmeair/apps/
```

* Change $WLP_SERVERDIR/acmeair/server.xml to:  
```bash
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
         <feature>jaxrs-2.0</feature>
         <feature>managedBeans-1.0</feature>
         <feature>cdi-1.2</feature>
    </featureManager>

    <!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
    <httpEndpoint id="defaultHttpEndpoint"
                  host="*"
                  httpPort="9085"
                  httpsPort="9485" />

    <application id="acmeair-webapp" name="acmeair-webapp" type="war" location="acmeair-webapp-2.0.0-SNAPSHOT.war">
      
    </application>

    <jndiEntry jndiName="com/acmeair/repository/type" value="mongo"/>

</server>
```

# MicroService Mode: Create the Five WebSphere Liberty servers

Windows:
```text
cd %WLP_SERVERDIR%
bin\server.bat create acemair-mainapp
bin\server.bat create acemair-as
bin\server.bat create acemair-bs
bin\server.bat create acemair-cs
bin\server.bat create acemair-fs
```

Linux:
```text
cd $WLP_SERVERDIR
in/server create acmeair-mainapp
bin/server create acmeair-as
bin/server create acmeair-bs
bin/server create acmeair-cs
bin/server create acmeair-fs
```

* Copy the web applications you previously built  

Windows:
```text
copy %ACMEAIR_SRCDIR%\acmeair-mainapp\build\libs\acmeair-mainapp-2.0.0-SNAPSHOT.war %WLP_SERVERDIR%\usr\servers\acmeair-mainapp\apps\
copy %ACMEAIR_SRCDIR%\acmeair-as\build\libs\acmeair-as-2.0.0-SNAPSHOT.war %WLP_SERVERDIR%\usr\servers\acmeair-as\apps\
copy %ACMEAIR_SRCDIR%\acmeair-bs\build\libs\acmeair-bs-2.0.0-SNAPSHOT.war %WLP_SERVERDIR%\usr\servers\acmeair-bs\apps\
copy %ACMEAIR_SRCDIR%\acmeair-cs\build\libs\acmeair-cs-2.0.0-SNAPSHOT.war %WLP_SERVERDIR%\usr\servers\acmeair-cs\apps\.
copy %ACMEAIR_SRCDIR%\acmeair-fs\build\libs\acmeair-fs-2.0.0-SNAPSHOT.war %WLP_SERVERDIR%\usr\servers\acmeair-fs\apps\.
```

Linux:
```text
cp $ACMEAIR_SRCDIR/acmeair-mainapp/build/libs/acmeair-mainapp-2.0.0-SNAPSHOT.war $WLP_SERVERDIR/usr/servers/acmeair-mainapp/apps/
cp $ACMEAIR_SRCDIR/acmeair-as/build/libs/acmeair-as-2.0.0-SNAPSHOT.war $WLP_SERVERDIR/usr/servers/acmeair-as/apps/
cp $ACMEAIR_SRCDIR/acmeair-bs/build/libs/acmeair-bs-2.0.0-SNAPSHOT.war $WLP_SERVERDIR/usr/servers/acmeair-bs/apps/
cp $ACMEAIR_SRCDIR/acmeair-cs/build/libs/acmeair-cs-2.0.0-SNAPSHOT.war $WLP_SERVERDIR/usr/servers/acmeair-cs/apps/
cp $ACMEAIR_SRCDIR/acmeair-fs/build/libs/acmeair-fs-2.0.0-SNAPSHOT.war $WLP_SERVERDIR/usr/servers/acmeair-fs/apps/
```

* Change $WLP_SERVERDIR/acmeair-mainapp/server.xml to:  
```bash
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
         <feature>jaxrs-2.0</feature>
         <feature>managedBeans-1.0</feature>
         <feature>cdi-1.2</feature>
    </featureManager>

    <!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
    <httpEndpoint id="defaultHttpEndpoint"
                  host="*"
                  httpPort="9080"
                  httpsPort="9480" />

    <application id="acmeair" name="acmeair" type="war" location="acmeair-mainapp-2.0.0-SNAPSHOT.war">
      
    </application>

    <jndiEntry jndiName="com/acmeair/repository/type" value="mongo"/>

</server>
```

* Change $WLP_SERVERDIR/acmeair-as/server.xml to:  
```bash
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
         <feature>jaxrs-2.0</feature>
         <feature>managedBeans-1.0</feature>
         <feature>cdi-1.2</feature>
    </featureManager>

    <!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
    <httpEndpoint id="defaultHttpEndpoint"
                  host="*"
                  httpPort="9083"
                  httpsPort="9483" />

    <application id="acmeair-as" name="acmeair-as" type="war" location="acmeair-as-2.0.0-SNAPSHOT.war">
      
    </application>

    <jndiEntry jndiName="com/acmeair/repository/type" value="mongo"/>

</server>
```

* Change $WLP_SERVERDIR/acmeair-cs/server.xml to:  
```bash
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
         <feature>jaxrs-2.0</feature>
         <feature>managedBeans-1.0</feature>
         <feature>cdi-1.2</feature>
    </featureManager>

    <!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
    <httpEndpoint id="defaultHttpEndpoint"
                  host="*"
                  httpPort="9081"
                  httpsPort="9481" />

    <application id="acmeair-cs" name="acmeair-cs" type="war" location="acmeair-cs-2.0.0-SNAPSHOT.war">
      
    </application>

    <jndiEntry jndiName="com/acmeair/repository/type" value="mongo"/>

</server>
```

* Change $WLP_SERVERDIR/acmeair-fs/server.xml to:  
```bash
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
         <feature>jaxrs-2.0</feature>
         <feature>managedBeans-1.0</feature>
         <feature>cdi-1.2</feature>
    </featureManager>

    <!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
    <httpEndpoint id="defaultHttpEndpoint"
                  host="*"
                  httpPort="9082"
                  httpsPort="9482" />

    <application id="acmeair-fs" name="acmeair-fs" type="war" location="acmeair-fs-2.0.0-SNAPSHOT.war">
      
    </application>

    <jndiEntry jndiName="com/acmeair/repository/type" value="mongo"/>

</server>
```

* Change $WLP_SERVERDIR/acmeair-bs/server.xml to:  
```bash
<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

    <!-- Enable features -->
    <featureManager>
         <feature>jaxrs-2.0</feature>
         <feature>managedBeans-1.0</feature>
         <feature>cdi-1.2</feature>
    </featureManager>

    <!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
    <httpEndpoint id="defaultHttpEndpoint"
                  host="*"
                  httpPort="9084"
                  httpsPort="9484" />

    <application id="acmeair-bs" name="acmeair-bs" type="war" location="acmeair-bs-2.0.0-SNAPSHOT.war">
      
    </application>

    <jndiEntry jndiName="com/acmeair/repository/type" value="mongo"/>

</server>
```


## Monolithic Mode - Start the WebSphere Liberty server

* Start the WebSphere Liberty server
Windows:
```text
cd %WLP_SERVERDIR%
bin\server start acmeair
```

Linux:
```text
cd $WLP_SERVERDIR
bin/server start acmeair
```


## MicroService Mode - Start all the WebSphere Liberty servers with env variables

* Start the WebSphere Liberty servers  
Windows:  
```text
cd %WLP_SERVERDIR%

bin\server.bat start acmeair-mainapp
bin\server.bat start acmeair-as
bin\server.bat start acmeair-bs
bin\server.bat start acmeair-cs
bin\server.bat start acmeair-fs
```

Linux:
```text
cd $WLP_SERVERDIR
bin/server start acmeair-mainapp
bin/server start acmeair-as
bin/server start acmeair-bs
bin/server start acmeair-cs
bin/server start acmeair-fs
```

## Install nginx
-Start nginx with the nginx.conf provided

## Look at the application
* Load the following url:
```text
http://localhost/acmeair
```


## Now we will load sample data using the web loader

Click on the "configure the Acme Air environment." link at the bottom of the page, or alternatively go to 
```text
http://localhost:9085/acmeair-webapp/loader.html (monolithic)
http://localhost/acmeair/loader.html (micro-services)
```

You can change the value for how many customers you wish to have loaded.  The default of 200 customer to load will be displayed. 
* After clicking on the "Load the Database" button you should see output that indicates flights and customers (200) were loaded. 


You will now be able to log in, click on the "Acme Air Home" link at either the top or bottom of the page to return to the welcome page. 

* Login (use the provided credentials), search for flights (suggest today between Paris and New York), book the flights, use the checkin link to cancel the bookings one at a time, view your account profile


## (Optional) Acmeair Configuration Properties

By default, Acmeair is configured with following variables
* Flight related data caching - Disabled
* DB related variables


| Variable Name       | Value           |
| ------------- |:-------------:|
| hostname | localhost |
| port | 27017 |
| dbname | acmeair |
| username | None |
| password | None |
| connectionsPerHost | Default |
| minConnectionsPerHost | Default |
| maxWaitTime | Default |
| connectTimeout | Default |
| socketTimeout | Default |
| socketKeepAlive | Default |
| sslEnabled | Default |
| threadsAllowedToBlockForConnectionMultiplier | Default |

To change these variables, create ACMEAIR_PROPERTIES variables to point to a properties file (e.g. ACMEAIR_PROPERTIES=/opt/BLUEMIX/acmeair/acmeair.properties)

Sample properties file content:

```userFlightDataRelatedCaching=true
hostname=your.mongo.db.hostname
port=27017
dbname=acmeair
username=dbuser
password=1234
#connectionsPerHost=
#minConnectionsPerHost=
#maxWaitTime=
#connectTimeout=
#socketTimeout=
#socketKeepAlive=
#sslEnabled=
#threadsAllowedToBlockForConnectionMultiplier=```


