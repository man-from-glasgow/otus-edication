lazy val _version: String = scala.io.Source
  .fromFile("VERSION")
  .getLines
  .toList.head.takeWhile(_ != ';').trim

lazy val mainSettings = Seq(
  name := "spark-connector",
  version := _version,
  organization := "org.example",
  scalaVersion := "2.12.8"
)

val testcontainersScalaVersion = "0.38.8"
lazy val parser = (project in file(".")).
  settings(mainSettings: _*).
  settings {
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-sql" % "3.0.1",
      "org.postgresql" % "postgresql" % "42.2.18",
      "org.scalatest" %% "scalatest" % "3.2.2" % "test",
      "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % "test",
      "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % "test",
    )
  }
