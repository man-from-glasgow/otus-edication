package com.example.kafka

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util
import java.util.Properties
import scala.collection.JavaConverters._

object Consumer extends LazyLogging {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:29092")
  props.put("group.id", "consumer1")

  val offsetMsg = 5

  Thread.currentThread.setContextClassLoader(null)
  val consumer = new KafkaConsumer(props, new StringDeserializer, new StringDeserializer)

  def get(topic: String): Unit = {
    consumer.subscribe(List(topic).asJavaCollection)

    logger.info(s"Subscribed to topic: ${topic}")

    val partitions = consumer.partitionsFor(topic).asScala
    val topicList = new util.ArrayList[TopicPartition]()

    partitions.foreach {
      p =>
        topicList.add(new TopicPartition(p.topic(), p.partition()))
    }

    val messages = consumer.poll(Duration.ofSeconds(1))
    consumer.seekToEnd(topicList)

    val cnt = messages.count()
    logger.info("Total messages: " + cnt)

    val partitionOffest = new util.HashMap[String, Long]()
    for (partition <- topicList.asScala){
      partitionOffest.put(partition.toString, consumer.position(partition) - 1)
    }

    for (msg <- messages.asScala) {
      val key = s"${msg.topic()}-${msg.partition()}"

      if (msg.offset() >= partitionOffest.get(key) - offsetMsg) {
        println(s"key: ${key} | offset: ${msg.offset()} | msg: ${msg.value()}")
      }
    }

    consumer.close()
  }
}
