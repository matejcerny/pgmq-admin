lazy val root = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("."))
  .settings(
    sbtConfigFile := (ThisBuild / baseDirectory).value / "build.conf"
  )
