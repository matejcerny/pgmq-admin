package io.github.matejcerny.pgmqadmin.views

import io.github.matejcerny.pgmqadmin.views.Htmx.*
import pgmq4s.domain.Message.*
import pgmq4s.domain.QueueMetrics
import scalatags.Text.all.*
import scalatags.Text.tags2

object QueueDetailViews:

  def queueDetailContent(queueName: String, metrics: Option[QueueMetrics]): Frag =
    frag(
      breadcrumb(queueName),
      tabNav(queueName, active = "Detail"),
      metrics match
        case None    => p(s"""Queue "$queueName" not found.""")
        case Some(m) => metricsGrid(m)
    )

  def queueMessagesContent(queueName: String, messages: List[Inbound.Plain[String]]): Frag =
    frag(
      breadcrumb(queueName),
      tabNav(queueName, active = "Messages"),
      if messages.isEmpty then p("No messages in this queue.")
      else messagesTable(messages)
    )

  def queueSettingsContent(queueName: String): Frag =
    frag(
      breadcrumb(queueName),
      tabNav(queueName, active = "Settings"),
      p("Work in progress")
    )

  private def metricsGrid(m: QueueMetrics): Frag =
    div(cls := "grid")(
      metricCard("Queue Length", m.queueLength.toString),
      metricCard("Total Messages", m.totalMessages.toString),
      metricCard("Oldest Message Age", formatAge(m.oldestMsgAgeSec)),
      metricCard("Newest Message Age", formatAge(m.newestMsgAgeSec))
    )

  private def metricCard(label: String, value: String): Tag =
    tags2.article(
      tag("header")(small(label)),
      strong(style := "font-size: 1.5rem")(value)
    )

  private def formatAge(seconds: Option[Long]): String =
    seconds match
      case None    => "N/A"
      case Some(s) =>
        if s < 60 then s"${s}s"
        else if s < 3600 then s"${s / 60}m ${s % 60}s"
        else if s < 86400 then s"${s / 3600}h ${(s % 3600) / 60}m"
        else s"${s / 86400}d ${(s % 86400) / 3600}h"

  private def messagesTable(messages: List[Inbound.Plain[String]]): Tag =
    div(style := "overflow-x: auto")(
      table(cls := "striped")(
        thead(
          tr(
            th("Message ID"),
            th("Read Count"),
            th("Enqueued At"),
            th("Visibility Timeout"),
            th("Payload")
          )
        ),
        tbody(
          messages.map: msg =>
            tr(
              td(msg.id.toString),
              td(msg.readCount.toString),
              td(msg.enqueuedAt.toString),
              td(msg.visibleAt.toString),
              td(pre(code(msg.payload)))
            )
        )
      )
    )

  private def tabNav(queueName: String, active: String): Frag =
    tags2.nav(
      ul(
        li(if active == "Detail" then strong("Detail") else a(href := s"/queues/$queueName/detail")("Detail")),
        li(
          if active == "Messages" then strong("Messages")
          else a(href := s"/queues/$queueName/messages")("Messages")
        ),
        li(
          if active == "Settings" then strong("Settings")
          else a(href := s"/queues/$queueName/settings")("Settings")
        )
      )
    )

  private def breadcrumb(queueName: String): Frag =
    tags2.nav(attr("aria-label") := "breadcrumb")(
      ul(
        li(a(href := "/queues")("Queues")),
        li(queueName)
      )
    )
