name := """play-morphia"""
organization := "it.unifi.cerm"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "dev.morphia.morphia" % "morphia-core" % "2.2.7",
  "org.easytesting" % "fest-assert" % "1.4" % "test"
) 
