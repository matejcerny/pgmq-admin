package io.github.matejcerny.pgmqadmin.domain

import cats.data.EitherT
import cats.effect.IO
import cats.syntax.applicativeError.*
import cats.syntax.either.*

import scala.util.control.NoStackTrace

enum AppError extends NoStackTrace:
  case ValidationError(message: String)
  case DatabaseError(message: String)
  case NotFound(resource: String)

object AppError:

  extension [A](io: IO[A])
    def toDatabaseError: EitherT[IO, AppError, A] =
      io.attemptT.leftMap: throwable =>
        AppError.DatabaseError(Option(throwable.getMessage).getOrElse("Unknown error"))

  extension [A](either: Either[String, A])
    def toValidationError: Either[AppError, A] =
      either.leftMap(AppError.ValidationError.apply)

  extension [A](either: Either[AppError, A])
    def flatTraverse[B](f: A => IO[B]): EitherT[IO, AppError, B] =
      either.toEitherT[IO].flatMap(a => f(a).toDatabaseError)
