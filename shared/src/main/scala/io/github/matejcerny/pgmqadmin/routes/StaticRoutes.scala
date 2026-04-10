package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.config.StaticAssets
import io.github.matejcerny.pgmqadmin.endpoints.StaticEndpoints
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter

object StaticRoutes:

  def routes: HttpRoutes[IO] =
    val appCssEndpoint =
      StaticEndpoints.appCss.serverLogicSuccess[IO](_ => IO.pure(StaticAssets.appCss))
    val appJsEndpoint =
      StaticEndpoints.appJs.serverLogicSuccess[IO](_ => IO.pure(StaticAssets.appJs))

    Http4sServerInterpreter[IO]().toRoutes(
      List(appCssEndpoint, appJsEndpoint)
    )
