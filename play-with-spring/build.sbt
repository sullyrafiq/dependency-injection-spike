name := "play-with-spring"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.springframework" % "spring-context" % "3.2.11.RELEASE",
  "javax.inject" % "javax.inject" % "1"
)     

play.Project.playJavaSettings
