lazy val commonSettings = Seq(
  organization := "com.sungevity",
  version := "0.0.5",
  scalaVersion := "2.11.7"
)

lazy val launch = taskKey[Unit]("Launch battle!")

mainClass in (Compile, run) := Some("robocode.Robocode")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Xlint",
  "-Xfatal-warnings"
)

libraryDependencies ++= Nil

fork in run := true

javaOptions ++= Seq("-Xmx512m")

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "Sungevity Robocode"
  )
