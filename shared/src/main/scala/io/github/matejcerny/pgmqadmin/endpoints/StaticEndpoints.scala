package io.github.matejcerny.pgmqadmin.endpoints

import sttp.model.MediaType
import sttp.tapir.*

object StaticEndpoints:

  val appCss: Endpoint[Unit, Unit, Unit, String, Any] =
    endpoint.get
      .in("css" / "app.css")
      .out(stringBody.and(header("Content-Type", MediaType.TextCss.toString)))

  val appJs: Endpoint[Unit, Unit, Unit, String, Any] =
    endpoint.get
      .in("js" / "app.js")
      .out(stringBody.and(header("Content-Type", MediaType.TextJavascript.toString)))
