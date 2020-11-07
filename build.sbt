scalaVersion := "2.12.10"
name := "scala-prob"
organization := "JohannesGraner"
version := "1.0-SNAPSHOT"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % Test
libraryDependencies += "org.typelevel" %% "spire" % "0.17.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.1"
