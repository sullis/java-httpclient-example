mvn clean package
rm -f results.txt
java -jar target/java-httpclient-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar --input urls.txt
