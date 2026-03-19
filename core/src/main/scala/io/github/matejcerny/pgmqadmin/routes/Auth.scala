package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import cats.syntax.applicative.*
import cats.syntax.either.*
import io.github.matejcerny.pgmqadmin.auth.User
import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint.Full

trait Auth:

  def secure[I, O](endpoint: Endpoint[Option[String], I, Unit, O, Any])(
      logic: User => I => IO[Either[Unit, O]]
  ): Full[Option[String], User, I, Unit, O, Any, IO] =
    endpoint
      .serverSecurityLogic(_ => User.admin.asRight.pure[IO])
      .serverLogic(logic)
