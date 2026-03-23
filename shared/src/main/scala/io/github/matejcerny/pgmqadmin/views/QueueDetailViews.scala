package io.github.matejcerny.pgmqadmin.views

import scalatags.Text.all.*
import scalatags.Text.tags2

object QueueDetailViews:

  def queueDetailContent(queueName: String): Frag =
    frag(
      breadcrumb(queueName),
      tags2.nav(
        ul(
          li(a(href := s"/queues/$queueName/messages")("Messages")),
          li(a(href := s"/queues/$queueName/settings")("Settings"))
        )
      ),
      p("Work in progress")
    )

  def queueMessagesContent(queueName: String): Frag =
    frag(
      breadcrumb(queueName, Some("Messages")),
      p("Work in progress")
    )

  def queueSettingsContent(queueName: String): Frag =
    frag(
      breadcrumb(queueName, Some("Settings")),
      p("Work in progress")
    )

  private def breadcrumb(queueName: String, current: Option[String] = None): Frag =
    tags2.nav(attr("aria-label") := "breadcrumb")(
      ul(
        li(a(href := "/queues")("Queues")),
        current match
          case None       => li(queueName)
          case Some(page) => frag(li(a(href := s"/queues/$queueName/detail")(queueName)), li(page))
      )
    )
