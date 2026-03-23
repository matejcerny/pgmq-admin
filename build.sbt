lazy val root = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    sbtConfigFile := (ThisBuild / baseDirectory).value / "build.conf",
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "io.github.matejcerny.pgmqadmin.config"
  )
