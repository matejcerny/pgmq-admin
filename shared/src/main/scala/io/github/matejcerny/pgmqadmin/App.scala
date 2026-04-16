package io.github.matejcerny.pgmqadmin

import cats.effect.{ IO, IOApp }
import cats.syntax.semigroupk.*
import io.github.matejcerny.pgmqadmin.config.AppConfig
import io.github.matejcerny.pgmqadmin.routes.*
import io.github.matejcerny.pgmqadmin.services.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.typelevel.otel4s.metrics.Meter
import org.typelevel.otel4s.trace.Tracer
import pgmq4s.skunk.{ SkunkPgmqAdmin, SkunkPgmqInspector }
import skunk.Session

object App extends IOApp.Simple:

  import Meter.Implicits.noop
  import Tracer.Implicits.noop

  def run: IO[Unit] =
    val config = AppConfig.default

    Session
      .Builder[IO]
      .withHost(config.db.host)
      .withPort(config.db.port)
      .withUserAndPassword(config.db.user, config.db.password)
      .withDatabase(config.db.database)
      .pooled(config.db.poolSize)
      .flatMap: pool =>
        val admin = SkunkPgmqAdmin[IO](pool)
        val inspector = SkunkPgmqInspector[IO](pool)
        val queueService = QueueService(admin)
        val messageService = MessageService(inspector)
        val notificationService = NotificationService(admin)

        val routes =
          StaticRoutes.routes <+>
            DashboardRoutes.routes <+>
            QueueRoutes.routes(queueService, messageService, notificationService) <+>
            TopicRoutes.routes <+>
            MetricRoutes.routes

        EmberServerBuilder
          .default[IO]
          .withHost(config.server.host)
          .withPort(config.server.port)
          .withHttpApp(Router("/" -> routes).orNotFound)
          .build
      .useForever
