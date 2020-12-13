# Hadoop HDFS App

## Build & Prepare Hadoop cluster
* Run command
```bash
docker-compose up -d
```
* Fix `/etc/hosts`
```text
127.0.0.1 namenode
127.0.0.1 datanode
```
* Add sample dataset
```bash
docker exec namenode hdfs dfs -put /sample_data/stage /
```
* Check sample dataset in hdfs
```bash
docker exec namenode hdfs dfs -ls /stage
```

## Assembly JAR file for project:
* Run command
```bash
sbt assembly
```
* Get file `hdfs-app-assembly-{VERSION}.jar` in directory `target/scala-2.12/`

## Run app
* Run command
```bash
java -jar hdfs-app-assembly-{VERSION}.jar
```