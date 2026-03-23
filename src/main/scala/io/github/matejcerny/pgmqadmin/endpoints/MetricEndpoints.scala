package io.github.matejcerny.pgmqadmin.endpoints

import io.github.matejcerny.pgmqadmin.endpoints.AuthEndpoint.{ AuthenticatedEndpoint, authenticated }
import sttp.tapir.*

object MetricEndpoints:

  val metricsPage: AuthenticatedEndpoint[Unit] =
    authenticated.get
      .in("metrics")
      .out(htmlBodyUtf8)
