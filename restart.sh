#! /bin/zsh
echo 'mvn clean install'
./mvnw clean install

echo 'cp jar file'
cp ./target/*.jar ./lib/

echo 'restart docker'
docker-compose stop; docker-compose rm -f; docker-compose up -d;
