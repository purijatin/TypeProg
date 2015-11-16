name := "scalaprac2"

version := "1.0"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

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

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0"
)

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"

libraryDependencies += "com.typesafe.akka" % "akka-stream-experimental_2.11" % "2.0-M1"

libraryDependencies += "com.typesafe.akka" % "akka-http-core-experimental_2.11" % "2.0-M1"

libraryDependencies += "com.sparkjava" % "spark-core" % "2.3"

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.4"

libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.4"


//set scalacOptions in (Compile, console) := "-Xprint:typer"

