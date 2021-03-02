# db2jccrecreate
`./gradlew jar`  
`docker-compose build`  
`docker-compose up -d`  
wait 5 minutes for DB2 to start/configure  
`docker exec -it app /bin/bash -l -c "java -jar /app.jar"`  
`docker-compose down`