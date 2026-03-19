package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.views.*
import org.http4s.HttpRoutes
import pgmq4s.PgmqAdmin
import sttp.tapir.server.http4s.Http4sServerInterpreter

object QueueRoutes:

  def make(admin: PgmqAdmin[IO]): HttpRoutes[IO] =

    val queuesPageEndpoint =
      Endpoints.queuesPage.serverLogic { _ => (_: Unit) =>
        admin.listQueues.map { queues =>
          Right(Layout.fullPage("Queues", QueueViews.queuesContent(queues)))
        }
      }

    val queuesTableEndpoint =
      Endpoints.queuesTable.serverLogic { _ => (_: Unit) =>
        admin.listQueues.map { queues =>
          Right(QueueViews.queuesTableHtml(queues).render)
        }
      }

    Http4sServerInterpreter[IO]().toRoutes(
      List(queuesPageEndpoint, queuesTableEndpoint)
    )
