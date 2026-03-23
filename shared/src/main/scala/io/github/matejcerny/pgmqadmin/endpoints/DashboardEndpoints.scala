package io.github.matejcerny.pgmqadmin.endpoints

import io.github.matejcerny.pgmqadmin.endpoints.AuthEndpoint.{ AuthenticatedEndpoint, authenticated }
import sttp.tapir.*

object DashboardEndpoints:

  val dashboardPage: AuthenticatedEndpoint[Unit] =
    authenticated.get
      .in("")
      .out(htmlBodyUtf8)
