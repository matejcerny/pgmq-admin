import Dependencies._

ThisBuild / tlBaseVersion := "0.1"
ThisBuild / scalaVersion := "3.8.2"
ThisBuild / organization := "io.github.matejcerny"
ThisBuild / startYear := Some(2026)
ThisBuild / tlJdkRelease := None

lazy val root = project
  .in(file("."))
  .aggregate(core.jvm)
  .settings(
    name := "pgmq-admin",
    publish / skip := true
  )

lazy val core = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "pgmq-admin-core",
    libraryDependencies ++= Seq(
      Modules.Typelevel %%% "cats-effect" % Versions.CatsEffect,
      Modules.Http4s %%% "http4s-ember-server" % Versions.Http4s,
      Modules.Skunk %%% "skunk-core" % Versions.Skunk,
      Modules.Pgmq4s %%% "pgmq4s-core" % Versions.Pgmq4s,
      Modules.Pgmq4s %%% "pgmq4s-skunk" % Versions.Pgmq4s,
      Modules.Pgmq4s %%% "pgmq4s-jsoniter" % Versions.Pgmq4s,
      Modules.Tapir %%% "tapir-core" % Versions.Tapir,
      Modules.Tapir %% "tapir-http4s-server" % Versions.Tapir,
      Modules.Outr %%% "scribe" % Versions.Scribe,
      Modules.Lihaoyi %%% "scalatags" % Versions.ScalaTags
    )
  )
  .jvmSettings(
    // tapir and scalatags do not publish native0.4 artifacts
    libraryDependencies ++= Seq(
      Modules.Outr %%% "scribe-slf4j" % Versions.Scribe
    )
  )
