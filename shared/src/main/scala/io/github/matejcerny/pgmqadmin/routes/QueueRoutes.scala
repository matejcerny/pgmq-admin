package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.endpoints.QueueEndpoints.*
import io.github.matejcerny.pgmqadmin.model.{ SortColumn, SortDir, SortState }
import io.github.matejcerny.pgmqadmin.views.*
import org.http4s.HttpRoutes
import pgmq4s.{ PgmqAdmin, QueueInfo, QueueName }
import sttp.tapir.server.http4s.Http4sServerInterpreter

object QueueRoutes extends Auth:

  def routes(admin: PgmqAdmin[IO]): HttpRoutes[IO] =

    val queuesPageEndpoint =
      secure(queuesPage): _ =>
        (_: Unit) =>
          admin.listQueues.map: queues =>
            Right(View.fullPage("Queues", "Queues", QueueViews.queuesContent(queues)))

    val queuesTableEndpoint =
      secure(queuesTable): _ =>
        (sortBy: Option[String], sortDir: Option[String]) =>
          admin.listQueues.map: queues =>
            val sort: Option[SortState] = SortState.from(sortBy, sortDir)
            Right(QueueViews.queuesTableHtml(sortQueues(queues, sort), sort).render)

    val queueDetailEndpoint =
      secure(queueDetail): _ =>
        (queueName: String) =>
          IO.pure(Right(View.fullPage("Queues", s"Queue: $queueName", QueueDetailViews.queueDetailContent(queueName))))

    val queueMessagesEndpoint =
      secure(queueMessages): _ =>
        (queueName: String) =>
          IO.pure(
            Right(
              View.fullPage("Queues", s"Queue: $queueName - Messages", QueueDetailViews.queueMessagesContent(queueName))
            )
          )

    val queueSettingsEndpoint =
      secure(queueSettings): _ =>
        (queueName: String) =>
          IO.pure(
            Right(
              View.fullPage("Queues", s"Queue: $queueName - Settings", QueueDetailViews.queueSettingsContent(queueName))
            )
          )

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
        queueDetailEndpoint,
        queueMessagesEndpoint,
        queueSettingsEndpoint,
        deleteQueueEndpoint,
        purgeQueueEndpoint,
        createQueueEndpoint
      )
    )

  private def sortQueues(queues: List[QueueInfo], sort: Option[SortState]): List[QueueInfo] =
    sort match
      case None                         => queues
      case Some(SortState(column, dir)) =>
        val sorted = column match
          case SortColumn.Name      => queues.sortBy(_.queueName.toString)
          case SortColumn.CreatedAt => queues.sortBy(_.createdAt.toString)
        dir match
          case SortDir.Asc  => sorted
          case SortDir.Desc => sorted.reverse
