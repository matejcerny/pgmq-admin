package io.github.matejcerny.pgmqadmin.endpoints

import sttp.tapir.*

object AuthEndpoint:

  type AuthenticatedEndpoint = Endpoint[Option[String], Unit, Unit, String, Any]

  val authenticated: Endpoint[Option[String], Unit, Unit, Unit, Any] =
    endpoint
      .securityIn(cookie[Option[String]]("session"))
