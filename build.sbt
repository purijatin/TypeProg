name := "scalaprac2"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

resolvers += "Nexus " at "http://maven.ia55.net/arcesium"

incOptions := incOptions.value.withNameHashing(false)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.2.4"
)

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.1.1",
  "io.circe" %% "circe-generic" % "0.1.1",
  "io.circe" %% "circe-jawn" % "0.1.1"
)

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"

libraryDependencies += "com.typesafe.akka" % "akka-stream-experimental_2.11" % "2.0-M1"

libraryDependencies += "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "2.0-M1"

libraryDependencies += "com.sparkjava" % "spark-core" % "2.3"

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.4"

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.4"


libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.7.5"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.5"

// https://mvnrepository.com/artifact/org.iq80.snappy/snappy
libraryDependencies += "org.iq80.snappy" % "snappy" % "0.4"

//libraryDependencies += "deshaw.codex" % "codex" % "20160406"
// https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper
libraryDependencies += "org.apache.zookeeper" % "zookeeper" % "3.4.9"

libraryDependencies += "org.apache.activemq" % "activemq-all" % "5.14.1" withSources ()


