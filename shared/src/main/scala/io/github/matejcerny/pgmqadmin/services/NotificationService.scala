package io.github.matejcerny.pgmqadmin.services

import cats.data.EitherT
import cats.effect.IO
import io.github.matejcerny.pgmqadmin.domain.AppError
import io.github.matejcerny.pgmqadmin.domain.AppError.*
import pgmq4s.PgmqAdmin
import pgmq4s.domain.{ NotifyThrottle, QueueName, ThrottleInterval }

import scala.concurrent.duration.*

class NotificationService(admin: PgmqAdmin[IO]):

  def getThrottleState(queueName: String): EitherT[IO, AppError, Option[NotifyThrottle]] =
    QueueName(queueName).toValidationError
      .flatTraverse: _ =>
        findThrottle(queueName)

  def enableNotifyInsert(queueName: String, throttleMs: Option[Int]): EitherT[IO, AppError, Option[NotifyThrottle]] =
    val validated =
      for
        qn <- QueueName(queueName)
        throttle <- ThrottleInterval(throttleMs.getOrElse(250).millis)
      yield (qn, throttle)

    validated.toValidationError
      .flatTraverse: (validatedName, validatedThrottle) =>
        admin.enableNotifyInsert(validatedName, validatedThrottle) *> findThrottle(queueName)

  def disableNotifyInsert(queueName: String): EitherT[IO, AppError, Unit] =
    QueueName(queueName).toValidationError
      .flatTraverse: validatedName =>
        admin.disableNotifyInsert(validatedName)

  def updateNotifyInsert(queueName: String, throttleMs: Int): EitherT[IO, AppError, Option[NotifyThrottle]] =
    val validated =
      for
        qn <- QueueName(queueName)
        throttle <- ThrottleInterval(throttleMs.millis)
      yield (qn, throttle)

    validated.toValidationError
      .flatTraverse: (validatedName, validatedThrottle) =>
        admin.updateNotifyInsert(validatedName, validatedThrottle) *> findThrottle(queueName)

  def purgeWithNotifyState(queueName: String): EitherT[IO, AppError, Option[NotifyThrottle]] =
    QueueName(queueName).toValidationError
      .flatTraverse: validatedName =>
        admin.purgeQueue(validatedName) *> findThrottle(queueName)

  private def findThrottle(queueName: String): IO[Option[NotifyThrottle]] =
    admin.listNotifyInsertThrottles.map(_.find(_.queueName.toString == queueName))
