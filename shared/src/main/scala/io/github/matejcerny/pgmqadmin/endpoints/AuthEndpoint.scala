package io.github.matejcerny.pgmqadmin.endpoints

import sttp.tapir.*

object AuthEndpoint:

  type AuthenticatedEndpoint[I] = Endpoint[Option[String], I, String, String, Any]

  val authenticated: Endpoint[Option[String], Unit, String, Unit, Any] =
    endpoint
      .securityIn(cookie[Option[String]]("session"))
      .errorOut(stringBody)
