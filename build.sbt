name := "CRUD"

version := "1.0"

lazy val `crud` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws ,
  "com.typesafe.slick" %% "slick"      % "3.0.0",
  "org.slf4j"           % "slf4j-nop"   % "1.6.4",
  "postgresql"          % "postgresql"  % "9.1-901.jdbc4")