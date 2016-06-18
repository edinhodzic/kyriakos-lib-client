import sbt._

organization := "io.kyriakos.library"

name := "kyriakos-lib-client"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val kyriakosLibCrudClient = project.in(file("."))

libraryDependencies ++= Seq(
  // scala
  "org.scala-lang" % "scala-library" % "2.11.7",
  // kyriakos
  "io.kyriakos.library" % "kyriakos-lib-domain_2.11" % "0.5.0-SNAPSHOT",
  "io.kyriakos.library" % "kyriakos-lib-crud_2.11" % "0.1.0-SNAPSHOT",
  // json4s
  "org.json4s" % "json4s-native_2.11" % "3.3.0",
  "org.json4s" % "json4s-ext_2.11" % "3.3.0",
  "org.json4s" % "json4s-jackson_2.11" % "3.3.0",
  // typesafe
  "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.1.0",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "2.0.1",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.0.1",
  // kamon
  "io.kamon" % "kamon-core_2.11" % "0.5.2",
  // test
  "org.specs2" % "specs2-core_2.11" % "3.6.6" % "test",
  "org.specs2" % "specs2-junit_2.11" % "3.6.6" % "test",
  "org.specs2" % "specs2-mock_2.11" % "3.6.6" % "test"
)

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}

scalacOptions ++= Seq("-deprecation", "-feature")
