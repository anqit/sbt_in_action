ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

name := "sbt_in_action"

//lazy val root = (project in file("."))
//  .settings(
//  )

libraryDependencies += "org.specs2" %% "specs2-core" % "5.2.0" % "test"
