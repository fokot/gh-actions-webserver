import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion     := "2.13.16"
ThisBuild / version          := "0.2.0-SNAPSHOT"
ThisBuild / organization     := "com.fokot"
ThisBuild / organizationName := "webserver"
ThisBuild / resolvers += "Sonatype Nexus Repository Manager" at "https://repository.cloudfarms.online/repository/maven-workshop/"
ThisBuild / resolvers += "Sonatype Nexus Repository Manager snapshots" at "https://repository.cloudfarms.online/repository/maven-workshop/"

val credentialsViaEnvVariables = for {
  username <- sys.env.get("MAVEN_WRITE_USERNAME")
  password <- sys.env.get("MAVEN_WRITE_PASSWORD")
} yield Credentials("Sonatype Nexus Repository Manager", "repository.cloudfarms.online", username, password)

ThisBuild / credentials += credentialsViaEnvVariables.getOrElse(Credentials(Path.userHome / ".sbt" / ".credentials"))

lazy val root = (project in file("."))
  .settings(
    name := "gh-actions-webserver",
    libraryDependencies ++= Seq(
      munit % Test,
      "com.fokot" %% "gh-actions-library" % "0.2.0-SNAPSHOT",
      "dev.zio" %% "zio-http" % "3.2.0",
      "org.scalikejdbc" %% "scalikejdbc" % "4.3.2",
      "com.zaxxer" % "HikariCP" % "6.3.0",
      "org.postgresql" % "postgresql" % "42.7.5",
      "dev.zio" %% "zio-test" % "2.1.17" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.1.17" % Test,
      "com.github.sideeffffect" %% "zio-testcontainers" % "0.6.0" % Test
    ),
    run / fork := true,
  )

enablePlugins(JavaAppPackaging)
