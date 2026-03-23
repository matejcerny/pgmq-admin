val generateVersion = taskKey[Unit]("Generate version source file")

lazy val root = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(
    sbtConfigFile := (ThisBuild / baseDirectory).value / "build.conf",
    generateVersion := {
      val buildInfoFile = (ThisBuild / baseDirectory).value /
        "src" / "main" / "scala" / "io" / "github" / "matejcerny" / "pgmqadmin" / "config" / "package.scala"
      IO.write(
        buildInfoFile,
        s"""package io.github.matejcerny.pgmqadmin.config
           |
           |val AppVersion: String = "${version.value}"
           |""".stripMargin
      )
    },
    Compile / compile := (Compile / compile).dependsOn(generateVersion).value
  )
