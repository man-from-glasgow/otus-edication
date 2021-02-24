# Домашнее задание №7 «Применение Spark Ml & Structured Streaming»

## Задача

Написать Spark приложение на Scala, которое выполнит следующее:
1. Построит модель классификации Ирисов Фишера с помощью Spark ML и сохранит ее.
2. Написать приложение, которое читает данные из одного топика Kafka, применяет модель и возвращает данные в новый топик

## Документация для запуска приложения
### 1. Обучить модель
* Перейти к файлу проекта `src/main/scala/com/example/ml/main.scala`
* Запустить в IDEA `Run 'main'` с параметром `train-data`
* В директории `src/main/resources/model` получим сохраненную модель

### 2. Настроить кластер KAFKA
* Запустить кластер Kafka
```bash
docker-compose up -d
```
* Создать необходимые топики
```bash
docker exec homework_7_ml_broker_1 kafka-topics \
  --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 3 \
  --topic input

docker exec homework_7_ml_broker_1 kafka-topics \
  --create \
  --zookeeper zookeeper:2181 \
  --replication-factor 1 \
  --partitions 3 \
  --topic prediction
```

* Положить необходимые данные в топик `input`
```bash
docker exec homework_7_ml_broker_1 bash -c "/scripts/output.sh | kafka-console-producer --bootstrap-server localhost:9092 --topic input"
```

* Запустить топики для просмотра
```bash
docker exec homework_7_ml_broker_1 kafka-console-consumer \
--bootstrap-server localhost:9092 \
--topic input

docker exec homework_7_ml_broker_1 kafka-console-consumer \
--bootstrap-server localhost:9092 \
--topic prediction
```

### 3. Обучить модель
* Перейти к файлу проекта `src/main/scala/com/example/ml/main.scala`
* Запустить в IDEA `Run 'main'` без параметров
* Смотреть что данные появятся в топиках