package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.domain.MessageSortState
import io.github.matejcerny.pgmqadmin.endpoints.QueueEndpoints.*
import io.github.matejcerny.pgmqadmin.services.*
import io.github.matejcerny.pgmqadmin.views.*
import org.http4s.HttpRoutes
import pgmq4s.domain.pagination.PageSize
import sttp.tapir.server.http4s.Http4sServerInterpreter

object QueueRoutes extends Auth:

  def routes(
      queueService: QueueService,
      messageService: MessageService,
      notificationService: NotificationService
  ): HttpRoutes[IO] =

    val queuesPageEndpoint =
      secure(queuesPage): _ =>
        (_: Unit) =>
          queueService
            .listQueues(None, None)
            .map: (queues, _) =>
              View.fullPage("Queues", "Queues", QueueViews.queuesContent(queues))

    val queuesTableEndpoint =
      secure(queuesTable): _ =>
        (sortBy: Option[String], sortDir: Option[String]) =>
          queueService
            .listQueues(sortBy, sortDir)
            .map:
              QueueViews.queuesTableHtml(_, _).render

    val queueDetailEndpoint =
      secure(queueDetail): _ =>
        (queueName: String) =>
          for
            metrics <- queueService.getMetrics(queueName)
            notifyState <- notificationService.getThrottleState(queueName)
          yield View.fullPage(
            "Queues",
            s"Queue: $queueName",
            QueueDetailViews.queueDetailContent(queueName, metrics, notifyState)
          )

    val queueMessagesEndpoint =
      secure(queueMessages): _ =>
        (
            queueName: String,
            pageSizeParam: Option[String],
            cursor: Option[String],
            sortBy: Option[String],
            sortDir: Option[String]
        ) =>
          val pageSize = parsePageSize(pageSizeParam)
          val sortState = MessageSortState.from(sortBy, sortDir)
          messageService
            .browseMessages(queueName, pageSize, sortState.toSort, cursor)
            .map: page =>
              View.fullPage(
                "Queues",
                s"Queue: $queueName - Messages",
                QueueDetailViews.queueMessagesContent(queueName, page, sortState, pageSize)
              )

    val messagesTableEndpoint =
      secure(messagesTable): _ =>
        (
            queueName: String,
            pageSizeParam: Option[String],
            cursor: Option[String],
            sortBy: Option[String],
            sortDir: Option[String]
        ) =>
          val pageSize = parsePageSize(pageSizeParam)
          val sortState = MessageSortState.from(sortBy, sortDir)
          messageService
            .browseMessages(queueName, pageSize, sortState.toSort, cursor)
            .map:
              QueueDetailViews.messagesTableHtml(queueName, _, sortState, pageSize).render

    val enableNotifyInsertEndpoint =
      secure(enableNotifyInsert): _ =>
        (queueName: String, throttleMs: Option[Int]) =>
          notificationService
            .enableNotifyInsert(queueName, throttleMs)
            .map:
              QueueDetailViews.notifyInsertModalBody(queueName, _).render

    val disableNotifyInsertEndpoint =
      secure(disableNotifyInsert): _ =>
        (queueName: String) =>
          notificationService
            .disableNotifyInsert(queueName)
            .map: _ =>
              QueueDetailViews.notifyInsertModalBody(queueName, None).render

    val updateNotifyInsertEndpoint =
      secure(updateNotifyInsert): _ =>
        (queueName: String, throttleMs: Int) =>
          notificationService
            .updateNotifyInsert(queueName, throttleMs)
            .map:
              QueueDetailViews.notifyInsertModalBody(queueName, _).render

    val settingsPurgeQueueEndpoint =
      secure(settingsPurgeQueue): _ =>
        (queueName: String) =>
          notificationService
            .purgeWithNotifyState(queueName)
            .map:
              QueueDetailViews.settingsGrid(queueName, _, purged = true).render

    val deleteQueueEndpoint =
      secure(deleteQueue): _ =>
        (queueName: String) =>
          queueService
            .dropQueue(queueName)
            .map:
              QueueViews.queuesTableHtml(_).render

    val purgeQueueEndpoint =
      secure(purgeQueue): _ =>
        (queueName: String) =>
          queueService
            .purgeQueue(queueName)
            .map:
              QueueViews.queuesTableHtml(_).render

    val createQueueEndpoint =
      secure(createQueue): _ =>
        (queueName: String) =>
          queueService
            .createQueue(queueName)
            .map:
              QueueViews.queuesTableHtml(_).render

    Http4sServerInterpreter[IO]().toRoutes(
      List(
        queuesPageEndpoint,
        queuesTableEndpoint,
        queueDetailEndpoint,
        queueMessagesEndpoint,
        messagesTableEndpoint,
        enableNotifyInsertEndpoint,
        disableNotifyInsertEndpoint,
        updateNotifyInsertEndpoint,
        settingsPurgeQueueEndpoint,
        deleteQueueEndpoint,
        purgeQueueEndpoint,
        createQueueEndpoint
      )
    )

  private def parsePageSize(param: Option[String]): PageSize =
    param
      .flatMap(_.toIntOption)
      .flatMap:
        case 50 => Some(PageSize.Fifty)
        case 10 => Some(PageSize.Ten)
        case _  => None
      .getOrElse(PageSize.Ten)
