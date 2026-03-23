package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.endpoints.DashboardEndpoints.*
import io.github.matejcerny.pgmqadmin.views.*
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter

object DashboardRoutes extends Auth:

  def routes: HttpRoutes[IO] =

    val dashboardPageEndpoint =
      secure(dashboardPage): _ =>
        (_: Unit) => IO.pure(Right(View.fullPage("Dashboard", "Dashboard", DashboardViews.dashboardContent)))

    Http4sServerInterpreter[IO]().toRoutes(
      List(dashboardPageEndpoint)
    )
