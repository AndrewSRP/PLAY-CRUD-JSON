name := "CRUD"

version := "1.0-SNAPSHOT"

lazy val `crud` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(jdbc, cache, ws
  ,"com.typesafe.play" %% "anorm" % "2.4.0" // version change
  ,"com.typesafe.slick" %% "slick"      % "3.0.0"
  ,"com.typesafe.play" %% "play-slick" % "1.0.0"
  ,"org.slf4j"           % "slf4j-nop"   % "1.6.4"
  ,"postgresql"          % "postgresql"  % "9.1-901.jdbc4")

libraryDependencies += specs2 % Test

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"