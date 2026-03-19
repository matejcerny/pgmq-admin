package io.github.matejcerny.pgmqadmin.auth

import cats.effect.IO
import sttp.tapir.*
import sttp.tapir.server.PartialServerEndpoint

object FakeAuth:

  val authenticated: PartialServerEndpoint[Option[String], User, Unit, Unit, Unit, Any, IO] =
    endpoint
      .securityIn(cookie[Option[String]]("session"))
      .serverSecurityLogic[User, IO](_ => IO.pure(Right(User.admin)))
