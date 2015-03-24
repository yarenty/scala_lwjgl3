version := "1.0.0"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"

libraryDependencies += "com.netflix.rxjava" % "rxjava-scala" % "0.15.0"

libraryDependencies += "org.json4s" % "json4s-native_2.10" % "3.2.5"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.3"

libraryDependencies += "net.databinder.dispatch" % "dispatch-core_2.10" % "0.11.0"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.3"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.5"

libraryDependencies += "com.squareup.retrofit" % "retrofit" % "1.0.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.0-M2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.2.3"

// Read here for optional jars and dependencies - specs2
libraryDependencies += "org.specs2" %% "specs2-core" % "3.1" % "test"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += "Local Maven Repository" at ""+Path.userHome.asFile.toURI.toURL+".m2/repository"

scalacOptions in Test ++= Seq("-Yrangepos")

// append several options to the list of options passed to the Java compiler
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-g")
