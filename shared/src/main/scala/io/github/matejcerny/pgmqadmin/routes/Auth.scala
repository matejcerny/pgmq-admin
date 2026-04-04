package io.github.matejcerny.pgmqadmin.routes

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.applicative.*
import cats.syntax.either.*
import io.github.matejcerny.pgmqadmin.domain.User
import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint.Full

trait Auth:

  def secure[I, O](endpoint: Endpoint[Option[String], I, String, O, Any])(
      logic: User => I => EitherT[IO, String, O]
  ): Full[Option[String], User, I, String, O, Any, IO] =
    endpoint
      .serverSecurityLogic(_ => User.admin.asRight.pure[IO])
      .serverLogic(user => (input: I) => logic(user)(input).value)
