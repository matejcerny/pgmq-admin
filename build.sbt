lazy val root = crossProject(JVMPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    sbtConfigFile := (ThisBuild / baseDirectory).value / "build.conf",
    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "io.github.matejcerny.pgmqadmin.config",
    Compile / sourceGenerators += Def.task {
      val resourceDir = (ThisBuild / baseDirectory).value / "shared" / "src" / "main" / "resources"
      val cssContent  = IO.read(resourceDir / "css" / "app.css")
      val jsContent   = IO.read(resourceDir / "js" / "app.js")
      val outDir      = (Compile / sourceManaged).value / "io" / "github" / "matejcerny" / "pgmqadmin" / "config"
      val outFile     = outDir / "StaticAssets.scala"
      val source =
        s"""|package io.github.matejcerny.pgmqadmin.config
            |
            |object StaticAssets:
            |  val appCss: String = \"\"\"$cssContent\"\"\"
            |  val appJs: String = \"\"\"$jsContent\"\"\"
            |""".stripMargin
      IO.write(outFile, source)
      Seq(outFile)
    }.taskValue
  )
