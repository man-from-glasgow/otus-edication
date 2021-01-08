lazy val _version: String = scala.io.Source
  .fromFile("VERSION")
  .getLines
  .toList.head.takeWhile(_ != ';').trim

lazy val mainSettings = Seq(
  name := "kafka-app",
  version := _version,
  organization := "com.example",
  scalaVersion := "2.12.8"
)

lazy val parser = (project in file(".")).
  settings(mainSettings: _*).
  settings {
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "org.json4s" %% "json4s-jackson" % "3.6.0",
      "org.apache.kafka" % "kafka-clients" % "2.6.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0",
    )
  }
