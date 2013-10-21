name := "scalado"

organization := "com.parascal"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "net.databinder.dispatch" %% "dispatch-json4s-native" % "0.11.0",
  "org.json4s" %% "json4s-native" % "3.2.5",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.5",
  "org.scalatest" % "scalatest_2.10" % "2.0.RC2" % "test"
)
