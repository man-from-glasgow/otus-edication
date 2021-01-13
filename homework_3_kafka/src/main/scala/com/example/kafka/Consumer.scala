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
  props.put("max.poll.records", "1000")

  val maxMsgForPartition = 5

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

    val partitionOffset = new util.HashMap[String, Long]()
    for (partition <- topicList.asScala) {
      partitionOffset.put(partition.toString, consumer.position(partition) - 1)
    }

    val buffer = new util.HashMap[String, List[String]]()
    for (msg <- messages.asScala) {
      val key = s"${msg.topic()}-${msg.partition()}"

      if (msg.offset() >= partitionOffset.get(key) - maxMsgForPartition && msg.offset() < partitionOffset.get(key)) {
        val msgWithOffset = s"offset: ${msg.offset()} | msg: ${msg.value()}"
        if (!buffer.containsKey(key)) {
          buffer.put(key, List(msgWithOffset))
        } else {
          val currentMsgList = buffer.get(key)
          if (currentMsgList.length <= maxMsgForPartition){
            val newMsgList = msgWithOffset :: currentMsgList
            buffer.put(key, newMsgList)
          }
        }
      }
    }

    for (partition <- topicList.asScala) {
      val key = partition.toString
      println("--------------")
      println(s"Topic: $key")
      println("--------------")
      buffer.getOrDefault(key, List()).foreach(println)
    }

    consumer.close()
  }
}