# shellcheck disable=SC1113
#/bin/bash

gnome-terminal -- docker-compose up

mvn clean && mvn install && mvn package && java -jar target/*.jar