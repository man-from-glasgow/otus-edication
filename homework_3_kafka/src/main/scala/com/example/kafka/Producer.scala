package com.example.kafka

import com.example.kafka.main.Book
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import org.json4s.jackson.JsonMethods.{compact, render}
import org.json4s.JsonDSL._

import java.util.Properties

object Producer {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:29092")

  Thread.currentThread.setContextClassLoader(null)
  val producer = new KafkaProducer(props, new StringSerializer, new StringSerializer)

  def sendMsg(booksInfo: Seq[Book], topic: String): Unit = {
    booksInfo.foreach {
      book =>
        val jsonMsg = ("name" -> book.name) ~
          ("author" -> book.author) ~
          ("userRating" -> book.userRating) ~
          ("reviews" -> book.reviews) ~
          ("price" -> book.price) ~
          ("year" -> book.year) ~
          ("genre" -> book.genre)

        val msg = compact(render(jsonMsg))

        producer.send(new ProducerRecord(topic, msg, msg))
    }

    producer.close()
  }
}
