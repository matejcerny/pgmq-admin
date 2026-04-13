package io.github.matejcerny.pgmqadmin.views

import io.github.matejcerny.pgmqadmin.domain.AppError
import scalatags.Text.all.*

object ErrorViews:

  def errorBanner(error: AppError): String =
    div(
      attr("role") := "alert",
      cls := "pico-background-red-200 alert-banner"
    )(errorMessage(error)).render

  private def errorMessage(error: AppError): String =
    error match
      case AppError.ValidationError(message) => message
      case AppError.DatabaseError(message)   => s"Database error: $message"
      case AppError.NotFound(resource)       => s"Not found: $resource"
