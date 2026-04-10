package io.github.matejcerny.pgmqadmin.routes

import cats.data.EitherT
import cats.effect.IO
import io.github.matejcerny.pgmqadmin.domain.AppError
import io.github.matejcerny.pgmqadmin.endpoints.MetricEndpoints.*
import io.github.matejcerny.pgmqadmin.views.*
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter

object MetricRoutes extends Auth:

  def routes: HttpRoutes[IO] =

    val metricsPageEndpoint =
      secure(metricsPage): _ =>
        (_: Unit) => EitherT.pure[IO, AppError](View.fullPage("Metrics", "Metrics", MetricViews.metricsContent))

    Http4sServerInterpreter[IO]().toRoutes(
      List(metricsPageEndpoint)
    )
