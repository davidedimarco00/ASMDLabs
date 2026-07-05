ThisBuild / version := "0.1.0-SNAPSHOT"
lazy val root = (project in file("."))
  .settings(
    name := "asmd-testing",
      libraryDependencies ++= Seq(
          "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
          "org.mockito" % "mockito-core" % "5.21.0" % Test)
          //"org.mockito" % "mockito-core" % "3.+" % Test)
)
