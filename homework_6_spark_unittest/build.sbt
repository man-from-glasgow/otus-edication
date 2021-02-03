lazy val _version: String = scala.io.Source
  .fromFile("VERSION")
  .getLines
  .toList.head.takeWhile(_ != ';').trim

lazy val mainSettings = Seq(
  name := "spark-unittest",
  version := _version,
  organization := "com.example",
  scalaVersion := "2.12.8"
)

val sparkVersion = "3.1.0"
lazy val parser = (project in file(".")).
  settings(mainSettings: _*).
  settings {
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion,
      "org.apache.logging.log4j" % "log4j-api" % "2.4.1",
      "org.apache.logging.log4j" % "log4j-core" % "2.4.1",
      "org.postgresql" % "postgresql" % "42.2.2",
      "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
      "org.apache.spark" %% "spark-sql" % sparkVersion % Test,
      "org.apache.spark" %% "spark-sql" % sparkVersion % Test classifier "tests",
      "org.apache.spark" %% "spark-catalyst" % sparkVersion % Test,
      "org.apache.spark" %% "spark-catalyst" % sparkVersion % Test classifier "tests",
      "org.apache.spark" %% "spark-hive" % sparkVersion % Test,
      "org.apache.spark" %% "spark-hive" % sparkVersion % Test classifier "tests",
      "org.apache.spark" %% "spark-core" % sparkVersion % Test,
      "org.apache.spark" %% "spark-core" % sparkVersion % Test classifier "tests",
      "org.scalatest" %% "scalatest" % "3.2.1" % Test,
    )
  }