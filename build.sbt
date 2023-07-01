import scala.sys.process.Process

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

name := "sbt_in_action"

val gitCommitHash = taskKey[String]("Determines the git commit SHA of this build")
ThisBuild / gitCommitHash := Process("git rev-parse HEAD").lineStream.head

val writeVersionProperties = taskKey[Seq[File]]("creates the version.properties build artifact")

Test / testOptions += Tests.Argument("html")

val testDependencies = Seq(
    "org.specs2" %% "specs2-core" % "5.2.0" % Test,
    "org.specs2" %% "specs2-html" % "5.2.0" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.64.8" % Test
)

def basicProject(name: String): Project =
    Project(name, file(name))
      .settings(
          version := "1.0",
          organization := "com.anqit",
          libraryDependencies ++= testDependencies
      )

lazy val common = basicProject("common") settings (
  writeVersionProperties := {
      val versionProps = (Compile / resourceManaged).value / "version.properties"
      val content = "version=%s" format gitCommitHash.value
      IO.write(versionProps, content)
      Seq(versionProps)
  },
)

lazy val analytics = basicProject("analytics") dependsOn common

lazy val website = basicProject("website") dependsOn common

lazy val multiMethods = basicProject("multi-methods") dependsOn common

//Compile / resourceGenerators += writeVersionProperties
