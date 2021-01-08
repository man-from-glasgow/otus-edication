# Kafka App

## Build & Prepare Kafka cluster
* Run command
```bash
docker-compose up -d
```
* Create new topic
```bash
docker exec homework_3_kafka_broker_1 kafka-topics \
  --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 3 \
  --topic books
```

## Assembly JAR file for project:
* Run command
```bash
sbt assembly
```
* Get file `kafka-app-assembly-{VERSION}.jar` in directory `target/scala-2.12/`

## Run app
* Run command
```bash
java -jar kafka-app-assembly-{VERSION}.jar
```