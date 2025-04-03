import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion     := "2.13.16"
ThisBuild / version          := "0.2.0-SNAPSHOT"
ThisBuild / organization     := "com.fokot"
ThisBuild / organizationName := "webserver"
ThisBuild / resolvers += "Sonatype Nexus Repository Manager" at "https://kodiak-helped-fawn.ngrok-free.app/repository/maven-public/"
ThisBuild / resolvers += "Sonatype Nexus Repository Manager snapshots" at "https://kodiak-helped-fawn.ngrok-free.app/repository/maven-snapshots/"

lazy val root = (project in file("."))
  .settings(
    name := "gh-actions-webserver",
    libraryDependencies ++= Seq(
      munit % Test,
      "com.fokot" %% "gh-actions-library" % "0.1.0-SNAPSHOT",
      "dev.zio" %% "zio-http" % "3.2.0"
    )
  )

enablePlugins(JavaAppPackaging)
