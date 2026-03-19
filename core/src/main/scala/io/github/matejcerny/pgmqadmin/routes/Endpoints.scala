package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.auth.{ FakeAuth, User }
import sttp.tapir.*
import sttp.tapir.server.PartialServerEndpoint

object Endpoints:

  val queuesPage: PartialServerEndpoint[Option[String], User, Unit, Unit, String, Any, IO] =
    FakeAuth.authenticated.get
      .in("queues")
      .out(htmlBodyUtf8)

  val queuesTable: PartialServerEndpoint[Option[String], User, Unit, Unit, String, Any, IO] =
    FakeAuth.authenticated.get
      .in("queues" / "table")
      .out(htmlBodyUtf8)
