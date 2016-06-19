import sbt._
import bintray.Keys._

organization := "io.kyriakos.library"

name := "kyriakos-lib-client"

version := "1.0.0"

scalaVersion := "2.11.7"

lazy val kyriakosLibCrudClient = project.in(file(".")).
  settings(bintrayPublishSettings: _*).
  settings(
    sbtPlugin := false,
    name := "kyriakos-lib-client",
    licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
    publishMavenStyle := false,
    repository in bintray := "kyriakos",
    bintrayOrganization in bintray := None
  )

resolvers += Resolver.url("edinhodzic", url("http://dl.bintray.com/edinhodzic/kyriakos"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  // scala
  "org.scala-lang" % "scala-library" % "2.11.7",
  // kyriakos
  "io.kyriakos.library" % "kyriakos-lib-domain_2.11" % "1.0.0",
  "io.kyriakos.library" % "kyriakos-lib-crud_2.11" % "1.0.0",
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
