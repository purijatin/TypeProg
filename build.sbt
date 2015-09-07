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