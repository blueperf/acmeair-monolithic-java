REGISTRY=registry.ng.bluemix.net
NAME_SPACE=strong
MONGO_BRIDGE=MongoBridge
SD_URL=https://servicediscovery.ng.bluemix.net
SD_TOKEN=1m3rliolucbampleoq36am82bdfv0othuruoefe6enop27ab7cnp

docker pull registry.ng.bluemix.net/ibmnode:javaee7

docker build -f ./acmeair-as/Dockerfile_BlueMix_as -t ${REGISTRY}/${NAME_SPACE}/acmeair_authservice .
docker build -f ./acmeair-bs/Dockerfile_BlueMix_bs -t ${REGISTRY}/${NAME_SPACE}/acmeair_bookingservice .
docker build -f ./acmeair-cs/Dockerfile_BlueMix_cs -t ${REGISTRY}/${NAME_SPACE}/acmeair_customerservice .
docker build -f ./acmeair-fs/Dockerfile_BlueMix_fs -t ${REGISTRY}/${NAME_SPACE}/acmeair_flightservice .

#docker push ${REGISTRY}/${NAME_SPACE}/acmeair_mainservice
docker push ${REGISTRY}/${NAME_SPACE}/acmeair_authservice
docker push ${REGISTRY}/${NAME_SPACE}/acmeair_bookingservice
docker push ${REGISTRY}/${NAME_SPACE}/acmeair_customerservice
docker push ${REGISTRY}/${NAME_SPACE}/acmeair_flightservice

cf ic run -m 256 -e SERVICE_NAME=main -e SD_URL=${SD_URL} -e SD_TOKEN=${SD_TOKEN} --name main_1 ${REGISTRY}/${NAME_SPACE}/acmeair_mainservice
cf ic run -m 256 -e CCS_BIND_APP=${MONGO_BRIDGE} -e SD_URL=${SD_URL} -e SD_TOKEN=${SD_TOKEN} --name Jauth_1     ${REGISTRY}/${NAME_SPACE}/acmeair_authservice
cf ic run -m 256 -e CCS_BIND_APP=${MONGO_BRIDGE} -e SD_URL=${SD_URL} -e SD_TOKEN=${SD_TOKEN} --name Jbooking_1  ${REGISTRY}/${NAME_SPACE}/acmeair_bookingservice
cf ic run -m 256 -e CCS_BIND_APP=${MONGO_BRIDGE} -e SD_URL=${SD_URL} -e SD_TOKEN=${SD_TOKEN} --name Jcustomer_1 ${REGISTRY}/${NAME_SPACE}/acmeair_customerservice
cf ic run -m 256 -e CCS_BIND_APP=${MONGO_BRIDGE} -e SD_URL=${SD_URL} -e SD_TOKEN=${SD_TOKEN} --name Jflight_1   ${REGISTRY}/${NAME_SPACE}/acmeair_flightservice


