name := "CodeReview"

version := "0.0.1"

scalaVersion := "2.10.3"

resolvers ++= Seq("spray repo" at "http://repo.spray.io",
  "eclipse repo" at "https://repo.eclipse.org/content/groups/releases/",
  "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository")

libraryDependencies ++= Seq("org.eclipse.jetty" % "jetty-server" % "9.0.6.v20130930",
  "org.eclipse.jetty" % "jetty-servlet" % "9.0.6.v20130930",
  "io.spray" % "spray-can" % "1.3.1",
  "io.spray" % "spray-routing" % "1.3.1",
  "io.spray" % "spray-servlet" % "1.3.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.0",
  "commons-io" % "commons-io" % "2.4",
  "pl.mpieciukiewicz.mpjsons" % "mpjsons" % "0.5", //clone from: https://github.com/marpiec/mpjsons and then mvn install
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.3.0.201403021825-r",
  "ch.qos.logback" % "logback-classic" % "1.1.1",
  "org.easytesting" % "fest-assert-core" % "2.0M10" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.0" % "test")


scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

seq(lessSettings: _*)