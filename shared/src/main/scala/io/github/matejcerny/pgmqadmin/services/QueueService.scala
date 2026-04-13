package io.github.matejcerny.pgmqadmin.services

import cats.data.EitherT
import cats.effect.IO
import io.github.matejcerny.pgmqadmin.domain.{ AppError, SortColumn, SortDir, SortState }
import io.github.matejcerny.pgmqadmin.domain.AppError.*
import pgmq4s.PgmqAdmin
import pgmq4s.domain.{ QueueInfo, QueueMetrics, QueueName }

class QueueService(admin: PgmqAdmin[IO]):

  def listQueues(
      sortBy: Option[String],
      sortDir: Option[String]
  ): EitherT[IO, AppError, (List[QueueInfo], Option[SortState])] =
    admin.listQueues.toDatabaseError.map: queues =>
      val sort = SortState.from(sortBy, sortDir)
      (sortQueues(queues, sort), sort)

  def getMetrics(queueName: String): EitherT[IO, AppError, Option[QueueMetrics]] =
    QueueName(queueName).toValidationError
      .flatTraverse: validatedName =>
        admin.metrics(validatedName)

  def createQueue(queueName: String): EitherT[IO, AppError, List[QueueInfo]] =
    QueueName(queueName).toValidationError
      .flatTraverse: validatedName =>
        admin.createQueue(validatedName) *> admin.listQueues

  def dropQueue(queueName: String): EitherT[IO, AppError, List[QueueInfo]] =
    QueueName(queueName).toValidationError
      .flatTraverse: validatedName =>
        admin.dropQueue(validatedName) *> admin.listQueues

  def purgeQueue(queueName: String): EitherT[IO, AppError, List[QueueInfo]] =
    QueueName(queueName).toValidationError
      .flatTraverse: validatedName =>
        admin.purgeQueue(validatedName) *> admin.listQueues

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
