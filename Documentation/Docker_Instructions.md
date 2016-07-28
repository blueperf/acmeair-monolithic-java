## Acmeair Liberty on Docker 


Assume you have 
1. [installed Docker and stared Docker daemon](https://docs.docker.com/installation/)
2. [Built the code base] (./Build_Instructions.md) 
	
		
#### Run Acmeair in Micro-Service Mode with Docker

	1. Create docker network
		docker network create --driver bridge my-net
	
	2. Build/Start Containers
		a. docker-compose build
		b. NETWORK=my_net docker-compose up
	
	3. Go to http://docker_machine_ip/main/acmeair
	




	