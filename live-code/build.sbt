name := """child-care"""

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
	  "com.typesafe.akka" %% "akka-actor" % "2.4.12"
)

fork in run := true
cancelable in Global := true
