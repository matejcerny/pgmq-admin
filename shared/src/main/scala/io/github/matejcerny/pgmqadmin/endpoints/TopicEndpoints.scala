package io.github.matejcerny.pgmqadmin.endpoints

import io.github.matejcerny.pgmqadmin.endpoints.AuthEndpoint.{ AuthenticatedEndpoint, authenticated }
import sttp.tapir.*

object TopicEndpoints:

  val topicsPage: AuthenticatedEndpoint[Unit] =
    authenticated.get
      .in("topics")
      .out(htmlBodyUtf8)
