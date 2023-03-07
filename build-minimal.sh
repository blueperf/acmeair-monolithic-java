docker build -t acmeair-mono-base-minimal -f Dockerfile-base-minimal --build-arg CONFIG_DIRECTORY=src/main/liberty/config --build-arg WAR_FILE=target/acmeair-monolithic-jakarta.war .
docker build -t acmeair-mono-app-minimal  -f Dockerfile-app-minimal --build-arg CONFIG_DIRECTORY=src/main/liberty/config --build-arg WAR_FILE=target/acmeair-monolithic-jakarta.war .
