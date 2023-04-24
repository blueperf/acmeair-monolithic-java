### Acme Air Monolith Sample and Benchmark for MongoDB

This application shows an implementation of a fictitious airline called "Acme Air" to exercise OpenLiberty JEE Profile with MongoDB as a datastore.

The application is forked from https://github.com/blueperf/acmeair-monolithic-java

The application depends on: 

> mongodb     | 4.4.18 | podmam pull icr.io/ppc64le-oss/mongodb-ppc64le:4.4.18
> openliberty | latest | podman pull icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi
> ubi-minimal | latest | podman pull registry.access.redhat.com/ubi8/ubi-minimal

### Build
Use maven to build the project
 - git clone https://github.com/prb112/acmeair-monolithic-java
 - cd acmeair-monolithic-java
 - cd source && mvn clean package && cd ..

Note, if you are on a mac, you can setup maven using `brew install maven` and `dnf install -y maven.noarch java-11-openjdk.x86_64`
Note, the tests are commented out as they depend on out of data MongoDb test dependencies.
 
### Setup

1. Create the MONGODB user and encode to base64.

```
❯ export MONGODB_USER=$(echo admin)
```

2. Create the MONGODB password and encode to base64. (an example)

```
❯ export MONGODDB_PASS=$(echo NOT_REAL )
```

3. Run the kustomize

```
❯ kustomize build manifests/overlays/single-arch --reorder none | oc apply -f -
```

# Database loading
 - Go to the home page http://hostname:port
 - At the bottom of the page, click the link : Configure the Acme Air Environment > Click **Load the database**
 
# Driving the load
 - Follow the instruction [here](https://github.com/blueperf/acmeair-driver)
 - Use performance/acmeair.jmx 
 - jmeter -n -t performance/acmeair.jmx -DusePureIDs=true -JHOST=hostname -JPORT=80 -j logName -JTHREAD=1 -JUSER=999 -JDURATION=60 -JRAMP=0 ;

## Contributing

If you have any questions or issues you can create a new [issue here][issues].

Pull requests are very welcome! Make sure your patches are well tested.
Ideally create a topic branch for every separate change you make. For
example:

1. Fork the repo
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Added some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## License

All source files must include a Copyright and License header. The SPDX license header is 
preferred because it can be easily scanned.

If you would like to see the detailed LICENSE click [here](LICENSE).

```text
#
# Copyright 2023 - IBM Corporation. All rights reserved
# SPDX-License-Identifier: Apache2.0
#
```

# Support
Is this a Red Hat or IBM supported solution?

No. This is only an sample application for Mixed Architecture Compute.

# Notes
The code may not use https://www.mongodb.com/docs/drivers/java/sync/current/fundamentals/connection/connect/
Thanks for DevOpsCube for https://devopscube.com/deploy-mongodb-kubernetes/