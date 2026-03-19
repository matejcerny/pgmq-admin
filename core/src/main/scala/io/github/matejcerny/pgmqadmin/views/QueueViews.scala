package io.github.matejcerny.pgmqadmin.views

import pgmq4s.QueueInfo
import scalatags.Text.all.*

object QueueViews:

  private val hxGet = attr("hx-get")
  private val hxTarget = attr("hx-target")
  private val hxSwap = attr("hx-swap")

  def queuesContent(queues: List[QueueInfo]): Frag =
    frag(
      div(
        style := "display: flex; justify-content: space-between; align-items: center;",
        h2("Queues"),
        button(
          hxGet := "/queues/table",
          hxTarget := "#queue-table-container",
          hxSwap := "innerHTML"
        )("Refresh")
      ),
      div(id := "queue-table-container")(
        queuesTableHtml(queues)
      )
    )

  def queuesTableHtml(queues: List[QueueInfo]): Tag =
    table(role := "grid")(
      thead(
        tr(
          th("Queue Name"),
          th("Created At")
        )
      ),
      tbody(
        queues.map: qi =>
          tr(
            td(qi.queueName.toString),
            td(qi.createdAt.toString)
          )
      )
    )
