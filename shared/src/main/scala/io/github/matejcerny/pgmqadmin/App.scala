package io.github.matejcerny.pgmqadmin

import cats.effect.{ IO, IOApp }
import cats.syntax.semigroupk.*
import io.github.matejcerny.pgmqadmin.config.AppConfig
import io.github.matejcerny.pgmqadmin.routes.{ DashboardRoutes, MetricRoutes, QueueRoutes, TopicRoutes }
import natchez.Trace
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import pgmq4s.skunk.SkunkPgmqAdmin
import skunk.Session

object App extends IOApp.Simple:

  import Trace.Implicits.noop

  def run: IO[Unit] =
    val config = AppConfig.default

    Session
      .pooled[IO](
        host = config.db.host,
        port = config.db.port,
        user = config.db.user,
        database = config.db.database,
        password = config.db.password,
        max = config.db.poolSize
      )
      .flatMap: pool =>
        val admin = SkunkPgmqAdmin[IO](pool)
        val routes =
          DashboardRoutes.routes <+>
            QueueRoutes.routes(admin) <+>
            TopicRoutes.routes <+>
            MetricRoutes.routes

        EmberServerBuilder
          .default[IO]
          .withHost(config.server.host)
          .withPort(config.server.port)
          .withHttpApp(Router("/" -> routes).orNotFound)
          .build
      .useForever
