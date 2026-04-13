package io.github.matejcerny.pgmqadmin.services

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.traverse.*
import io.github.matejcerny.pgmqadmin.domain.AppError
import io.github.matejcerny.pgmqadmin.domain.AppError.*
import pgmq4s.PgmqInspector
import pgmq4s.domain.*
import pgmq4s.domain.pagination.*

class MessageService(inspector: PgmqInspector[IO]):

  def browseMessages(
      queueName: String,
      pageSize: PageSize,
      sort: Sort[MessageSortField],
      cursor: Option[String]
  ): EitherT[IO, AppError, CursorPage[InspectedMessage]] =
    val validated =
      for
        qn <- QueueName(queueName)
        c <- cursor.traverse(Cursor.fromString)
      yield (qn, c)

    validated.toValidationError
      .flatTraverse: (validatedName, validatedCursor) =>
        inspector.browseMessages(validatedName, pageSize, sort, validatedCursor)
