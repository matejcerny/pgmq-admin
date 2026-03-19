package io.github.matejcerny.pgmqadmin.endpoints

import io.github.matejcerny.pgmqadmin.endpoints.AuthEndpoint.{ AuthenticatedEndpoint, authenticated }
import sttp.tapir.*

object QueueEndpoints:

  val queuesPage: AuthenticatedEndpoint =
    authenticated.get
      .in("queues")
      .out(htmlBodyUtf8)

  val queuesTable: AuthenticatedEndpoint =
    authenticated.get
      .in("queues" / "table")
      .out(htmlBodyUtf8)
