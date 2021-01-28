# Домашнее задание №5 «Spark DataSource API V2»

## Задача

Используя DataSource API V2 написать Spark Connector для PostgreSQL:
1. Доработать файл `PostgresqlDriver.scala` так, чтобы можно было читать таблицу users в несколько партиций.
2. Добавить в тест дополнительный параметр указывающий размер партиции`option("partitionSize", "10")`
3. Добавить проверку на количество партиции. Добиться прохождения теста

---

## Документация для запуска приложения

### Run spark postgresql datasource in IDEA
* Go to project test file `src/test/scala/org/example/PostgresqlDriverTest.scala`
* Run `Read Test`