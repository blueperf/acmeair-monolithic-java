REGISTRY=registry.ng.bluemix.net
NAME_SPACE=strong

docker build -f ./Dockerfile_BlueMix_webapp -t ${REGISTRY}/${NAME_SPACE}/acmeair_webapp .
docker push ${REGISTRY}/${NAME_SPACE}/acmeair_webapp
cf ic run --name webapp_1   ${REGISTRY}/${NAME_SPACE}/acmeair_webapp


