ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "game",
    libraryDependencies ++= Seq(
      "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
      "io.cucumber" % "cucumber-java" % "7.21.1" % Test,
      "io.cucumber" % "cucumber-junit-platform-engine" % "7.21.1" % Test,
      "org.junit.platform" % "junit-platform-suite" % "1.10.2" % Test
    )
  )
