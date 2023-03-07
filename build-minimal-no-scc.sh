docker build -t acmeair-mono-base-minimal-no-scc -f Dockerfile-base-minimal-no-scc --build-arg CONFIG_DIRECTORY=src/main/liberty/config --build-arg WAR_FILE=target/acmeair-monolithic-jakarta.war .
docker build -t acmeair-mono-app-minimal-no-scc  -f Dockerfile-app-minimal-no-scc  --build-arg CONFIG_DIRECTORY=src/main/liberty/config --build-arg WAR_FILE=target/acmeair-monolithic-jakarta.war .
