package io.github.matejcerny.pgmqadmin.services

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.either.*
import pgmq4s.PgmqAdmin
import pgmq4s.domain.{ NotifyThrottle, QueueName, ThrottleInterval }

import scala.concurrent.duration.*

class NotificationService(admin: PgmqAdmin[IO]):

  def getThrottleState(queueName: String): EitherT[IO, String, Option[NotifyThrottle]] =
    QueueName(queueName)
      .toEitherT[IO]
      .semiflatMap: _ =>
        findThrottle(queueName)

  def enableNotifyInsert(queueName: String, throttleMs: Option[Int]): EitherT[IO, String, Option[NotifyThrottle]] =
    val validated =
      for
        qn <- QueueName(queueName)
        t <- ThrottleInterval(throttleMs.getOrElse(250).millis)
      yield (qn, t)

    validated
      .toEitherT[IO]
      .semiflatMap: (qn, t) =>
        admin.enableNotifyInsert(qn, t) *> findThrottle(queueName)

  def disableNotifyInsert(queueName: String): EitherT[IO, String, Unit] =
    QueueName(queueName)
      .toEitherT[IO]
      .semiflatMap: qn =>
        admin.disableNotifyInsert(qn)

  def updateNotifyInsert(queueName: String, throttleMs: Int): EitherT[IO, String, Option[NotifyThrottle]] =
    val validated =
      for
        qn <- QueueName(queueName)
        t <- ThrottleInterval(throttleMs.millis)
      yield (qn, t)

    validated
      .toEitherT[IO]
      .semiflatMap: (qn, t) =>
        admin.updateNotifyInsert(qn, t) *> findThrottle(queueName)

  def purgeWithNotifyState(queueName: String): EitherT[IO, String, Option[NotifyThrottle]] =
    QueueName(queueName)
      .toEitherT[IO]
      .semiflatMap: qn =>
        admin.purgeQueue(qn) *> findThrottle(queueName)

  private def findThrottle(queueName: String): IO[Option[NotifyThrottle]] =
    admin.listNotifyInsertThrottles.map(_.find(_.queueName.toString == queueName))
