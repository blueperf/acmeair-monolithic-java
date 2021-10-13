docker build -t acmeair-mono-base-minimal -f Dockerfile-base-minimal --build-arg WAR_FILE=target/acmeair-java-2.0.0-SNAPSHOT.war .
docker build -t acmeair-mono-app-minimal  -f Dockerfile-app-minimal --build-arg WAR_FILE=target/acmeair-java-2.0.0-SNAPSHOT.war .
