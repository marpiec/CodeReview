resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
      Resolver.ivyStylePatterns)

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

addSbtPlugin("me.lessis" % "less-sbt" % "0.2.2")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")