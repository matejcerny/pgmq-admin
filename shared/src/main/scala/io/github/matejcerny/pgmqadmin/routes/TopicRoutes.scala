package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.endpoints.TopicEndpoints.*
import io.github.matejcerny.pgmqadmin.views.*
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter

object TopicRoutes extends Auth:

  def routes: HttpRoutes[IO] =

    val topicsPageEndpoint =
      secure(topicsPage): _ =>
        (_: Unit) => IO.pure(Right(View.fullPage("Topics", "Topics", TopicViews.topicsContent)))

    Http4sServerInterpreter[IO]().toRoutes(
      List(topicsPageEndpoint)
    )
