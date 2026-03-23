package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.endpoints.QueueEndpoints.*
import io.github.matejcerny.pgmqadmin.views.*
import org.http4s.HttpRoutes
import pgmq4s.{ PgmqAdmin, QueueName }
import sttp.tapir.server.http4s.Http4sServerInterpreter

object QueueRoutes extends Auth:

  def routes(admin: PgmqAdmin[IO]): HttpRoutes[IO] =

    val queuesPageEndpoint =
      secure(queuesPage): _ =>
        (_: Unit) =>
          admin.listQueues.map: queues =>
            Right(Layout.fullPage("Queues", QueueViews.queuesContent(queues)))

    val queuesTableEndpoint =
      secure(queuesTable): _ =>
        (_: Unit) =>
          admin.listQueues.map: queues =>
            Right(QueueViews.queuesTableHtml(queues).render)

    val deleteQueueEndpoint =
      secure(deleteQueue): _ =>
        (queueName: String) =>
          admin.dropQueue(QueueName(queueName)) *>
            admin.listQueues.map: queues =>
              Right(QueueViews.queuesTableHtml(queues).render)

    val purgeQueueEndpoint =
      secure(purgeQueue): _ =>
        (queueName: String) =>
          admin.purgeQueue(QueueName(queueName)) *>
            admin.listQueues.map: queues =>
              Right(QueueViews.queuesTableHtml(queues).render)

    val createQueueEndpoint =
      secure(createQueue): _ =>
        (queueName: String) =>
          admin.createQueue(QueueName(queueName)) *>
            admin.listQueues.map: queues =>
              Right(QueueViews.queuesTableHtml(queues).render)

    Http4sServerInterpreter[IO]().toRoutes(
      List(
        queuesPageEndpoint,
        queuesTableEndpoint,
        deleteQueueEndpoint,
        purgeQueueEndpoint,
        createQueueEndpoint
      )
    )
