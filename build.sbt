name := "CodeReview"

version := "0.0.1"

scalaVersion := "2.10.4"

resolvers ++= Seq("eclipse repo" at "https://repo.eclipse.org/content/groups/releases/",
  "marpiec BinTray" at "http://dl.bintray.com/marpiec/maven/",
  "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository")

libraryDependencies ++= Seq("org.eclipse.jetty" % "jetty-server" % "9.0.6.v20130930",
  "org.eclipse.jetty" % "jetty-servlet" % "9.0.6.v20130930",
  "org.scalatra" % "scalatra_2.10" % "2.3.0.M1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.0",
  "commons-io" % "commons-io" % "2.4",
  "org.apache.commons" % "commons-lang3" % "3.3.1",
  "com.github.nscala-time" %% "nscala-time" % "0.8.0",
  "com.h2database" % "h2" % "1.3.175",
  "com.typesafe" % "config" % "1.2.0",
  "pl.mpieciukiewicz.mpjsons" % "mpjsons" % "0.5.1",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "3.3.0.201403021825-r",
  "javax.mail" % "mail" % "1.4.7",
  "org.mockito" % "mockito-all" % "1.9.5",
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

//seq(lessSettings: _*)

//fork := true

//javaOptions := Seq("-DdevelopmentMode=true", "-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005")
