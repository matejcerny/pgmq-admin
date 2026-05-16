package io.github.matejcerny.pgmqadmin.services

import cats.data.EitherT
import cats.effect.IO
import io.github.matejcerny.pgmqadmin.domain.AppError
import io.github.matejcerny.pgmqadmin.domain.AppError.*
import pgmq4s.PgmqAdmin
import pgmq4s.domain.{ QueueName, RoutingKey, RoutingMatch, TopicPattern }

class TopicService(admin: PgmqAdmin[IO]):

  // TODO: replace with admin.listBindings when available in pgmq4s
  def listBindings(): EitherT[IO, AppError, List[RoutingMatch]] =
    EitherT.pure(List.empty)

  def bindTopic(pattern: String, queueName: String): EitherT[IO, AppError, Unit] =
    (for
      validatedPattern <- TopicPattern(pattern).toValidationError
      validatedQueueName <- QueueName(queueName).toValidationError
    yield (validatedPattern, validatedQueueName)).flatTraverse: (validatedPattern, validatedQueueName) =>
      admin.bindTopic(validatedPattern, validatedQueueName)

  def unbindTopic(pattern: String, queueName: String): EitherT[IO, AppError, Boolean] =
    (for
      validatedPattern <- TopicPattern(pattern).toValidationError
      validatedQueueName <- QueueName(queueName).toValidationError
    yield (validatedPattern, validatedQueueName)).flatTraverse: (validatedPattern, validatedQueueName) =>
      admin.unbindTopic(validatedPattern, validatedQueueName)

  def testRouting(routingKey: String): EitherT[IO, AppError, List[RoutingMatch]] =
    RoutingKey(routingKey).toValidationError.flatTraverse(admin.testRouting)
