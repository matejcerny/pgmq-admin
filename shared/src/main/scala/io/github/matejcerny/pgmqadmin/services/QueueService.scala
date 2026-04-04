package io.github.matejcerny.pgmqadmin.services

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.either.*
import io.github.matejcerny.pgmqadmin.domain.{ SortColumn, SortDir, SortState }
import pgmq4s.PgmqAdmin
import pgmq4s.domain.{ QueueInfo, QueueMetrics, QueueName }

class QueueService(admin: PgmqAdmin[IO]):

  def listQueues(
      sortBy: Option[String],
      sortDir: Option[String]
  ): EitherT[IO, String, (List[QueueInfo], Option[SortState])] =
    EitherT
      .liftF(admin.listQueues)
      .map: queues =>
        val sort = SortState.from(sortBy, sortDir)
        (sortQueues(queues, sort), sort)

  def getMetrics(queueName: String): EitherT[IO, String, Option[QueueMetrics]] =
    QueueName(queueName).toEitherT[IO].semiflatMap(admin.metrics)

  def createQueue(queueName: String): EitherT[IO, String, List[QueueInfo]] =
    QueueName(queueName)
      .toEitherT[IO]
      .semiflatMap: qn =>
        admin.createQueue(qn) *> admin.listQueues

  def dropQueue(queueName: String): EitherT[IO, String, List[QueueInfo]] =
    QueueName(queueName)
      .toEitherT[IO]
      .semiflatMap: qn =>
        admin.dropQueue(qn) *> admin.listQueues

  def purgeQueue(queueName: String): EitherT[IO, String, List[QueueInfo]] =
    QueueName(queueName)
      .toEitherT[IO]
      .semiflatMap: qn =>
        admin.purgeQueue(qn) *> admin.listQueues

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
