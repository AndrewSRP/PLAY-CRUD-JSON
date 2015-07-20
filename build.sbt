name := "CRUD"

version := "1.0-SNAPSHOT"

lazy val `crud` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(jdbc, cache, ws
  ,"com.typesafe.play" %% "anorm" % "2.4.0" // version change
  ,"com.typesafe.slick" %% "slick"      % "3.0.0"
  ,"com.typesafe.play" %% "play-slick" % "1.0.0"
  ,"com.zaxxer" % "HikariCP" % "2.3.8"
  ,"org.slf4j"           % "slf4j-nop"   % "1.6.4"
  //,"postgresql"          % "postgresql"  % "9.1-901.jdbc4")
  ,"org.postgresql" % "postgresql" % "9.4-1201-jdbc41")

libraryDependencies += specs2 % Test

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"