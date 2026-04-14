package io.github.matejcerny.pgmqadmin.views

import io.github.matejcerny.pgmqadmin.domain.MessageSortState
import io.github.matejcerny.pgmqadmin.views.Htmx.*
import pgmq4s.domain.pagination.{ CursorPage, InspectedMessage, MessageSortField, PageSize, SortDirection }
import pgmq4s.domain.{ NotifyThrottle, QueueMetrics }
import scalatags.Text.all.*
import scalatags.Text.tags2

object QueueDetailViews:

  def queueDetailContent(queueName: String, metrics: Option[QueueMetrics], notifyState: Option[NotifyThrottle]): Frag =
    frag(
      detailTabs(queueName, active = "Detail"),
      metrics match
        case None    => p(s"""Queue "$queueName" not found.""")
        case Some(m) =>
          frag(
            metricsGrid(m),
            settingsGrid(queueName, notifyState, purged = false)
          )
    )

  def queueMessagesContent(
      queueName: String,
      page: CursorPage[InspectedMessage],
      sortState: MessageSortState,
      pageSize: PageSize
  ): Frag =
    frag(
      detailTabs(queueName, active = "Messages"),
      div(id := "messages-container")(
        messagesTableHtml(queueName, page, sortState, pageSize)
      )
    )

  def messagesTableHtml(
      queueName: String,
      page: CursorPage[InspectedMessage],
      sortState: MessageSortState,
      pageSize: PageSize
  ): Tag =
    div(
      if page.items.isEmpty then
        frag(
          pageSizeToolbar(queueName, sortState, pageSize, 0),
          p("No messages in this queue.")
        )
      else
        frag(
          pageSizeToolbar(queueName, sortState, pageSize, page.items.size),
          messagesTable(queueName, page.items, sortState, pageSize),
          paginationControls(queueName, page, sortState, pageSize)
        )
    )

  def settingsGrid(queueName: String, notifyState: Option[NotifyThrottle], purged: Boolean): Frag =
    div(id := "settings-body")(
      if purged then
        div(
          attr("role") := "alert",
          cls := "pico-background-jade-200 alert-banner"
        )("Queue purged successfully.")
      else frag(),
      div(cls := "grid")(
        notifyInsertCard(queueName, notifyState),
        dangerZoneCard(queueName)
      ),
      notifyInsertModal(queueName, notifyState)
    )

  def notifyInsertCard(queueName: String, notifyState: Option[NotifyThrottle]): Tag =
    tags2.article(
      tag("header")(small("Notifications")),
      notifyState match
        case None =>
          p(small("Disabled"))
        case Some(nt) =>
          frag(
            p(small("Enabled")),
            p(small("Throttle: ", strong(s"${nt.throttleInterval.toMillis}ms"))),
            p(small("Last notified: ", nt.lastNotifiedAt.toString))
          ),
      tag("footer")(
        button(
          cls := "outline",
          attr("onclick") := s"document.getElementById('notify-modal-$queueName').showModal();"
        )("Configure")
      )
    )

  def notifyInsertModalBody(queueName: String, notifyState: Option[NotifyThrottle]): Tag =
    tags2.article(id := "notify-modal-content")(
      tag("header")(
        a(
          href := "#",
          attr("aria-label") := "Close",
          attr("rel") := "prev",
          attr("onclick") := "this.closest('dialog').close(); return false;"
        ),
        strong("Notifications")
      ),
      notifyState match
        case None =>
          p("Notifications are currently ", strong("disabled"), " for this queue.")
        case Some(nt) =>
          frag(
            p("Notifications are currently ", strong("enabled"), " for this queue."),
            label(
              "Throttle interval (ms)",
              input(
                tpe := "number",
                id := s"throttle-ms-$queueName",
                value := nt.throttleInterval.toMillis.toString,
                attr("min") := "1"
              )
            )
          ),
      notifyState match
        case None =>
          tag("footer")(
            button(
              cls := "secondary",
              attr("onclick") := "this.closest('dialog').close();"
            )("Cancel"),
            button(
              hxPost := s"/queues/$queueName/settings/notify-insert/enable?throttleMs=250",
              hxTarget := "#notify-modal-content",
              hxSwap := "outerHTML"
            )("Enable Notifications")
          )
        case Some(_) =>
          tag("footer")(
            button(
              cls := "secondary",
              attr("onclick") := "this.closest('dialog').close();"
            )("Cancel"),
            button(
              cls := "outline secondary",
              hxPost := s"/queues/$queueName/settings/notify-insert/disable",
              hxTarget := "#notify-modal-content",
              hxSwap := "outerHTML"
            )("Disable"),
            button(
              attr("onclick") := s"saveNotifyThrottle('$queueName');"
            )("Save")
          )
    )

  private def dangerZoneCard(queueName: String): Tag =
    tags2.article(
      tag("header")(small("Danger Zone")),
      p(small("Destructive operations for this queue.")),
      tag("footer")(
        div(cls := "button-row")(
          button(
            cls := "outline secondary",
            attr("onclick") := s"document.getElementById('purge-modal-$queueName').showModal();"
          )("Purge Queue"),
          button(
            cls := "outline secondary",
            attr("onclick") := s"document.getElementById('delete-modal-$queueName').showModal();"
          )("Delete Queue")
        )
      ),
      purgeModal(queueName),
      deleteModal(queueName)
    )

  private def notifyInsertModal(queueName: String, notifyState: Option[NotifyThrottle]): Tag =
    tag("dialog")(id := s"notify-modal-$queueName")(
      notifyInsertModalBody(queueName, notifyState)
    )

  private def purgeModal(queueName: String): Tag =
    tag("dialog")(id := s"purge-modal-$queueName")(
      tags2.article(
        tag("header")(
          a(
            href := "#",
            attr("aria-label") := "Close",
            attr("rel") := "prev",
            attr("onclick") := "this.closest('dialog').close(); return false;"
          ),
          strong("Purge Queue")
        ),
        p(s"""Are you sure you want to purge all messages from queue "$queueName"?"""),
        tag("footer")(
          button(
            cls := "secondary",
            attr("onclick") := "this.closest('dialog').close();"
          )("Cancel"),
          button(
            hxPost := s"/queues/$queueName/settings/purge",
            hxTarget := "#settings-body",
            hxSwap := "outerHTML",
            attr("onclick") := "this.closest('dialog').close();"
          )("Purge")
        )
      )
    )

  private def deleteModal(queueName: String): Tag =
    tag("dialog")(id := s"delete-modal-$queueName")(
      tags2.article(
        tag("header")(
          a(
            href := "#",
            attr("aria-label") := "Close",
            attr("rel") := "prev",
            attr("onclick") := "this.closest('dialog').close(); return false;"
          ),
          strong("Delete Queue")
        ),
        p(s"""Are you sure you want to delete queue "$queueName"? This action cannot be undone."""),
        tag("footer")(
          button(
            cls := "secondary",
            attr("onclick") := "this.closest('dialog').close();"
          )("Cancel"),
          button(
            hxDelete := s"/queues/$queueName",
            attr("hx-on::after-request") := "window.location.href = '/queues';",
            attr("onclick") := "this.closest('dialog').close();"
          )("Delete")
        )
      )
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
      strong(cls := "metric-value")(value)
    )

  private def formatAge(seconds: Option[Long]): String =
    seconds match
      case None    => "N/A"
      case Some(s) =>
        if s < 60 then s"${s}s"
        else if s < 3600 then s"${s / 60}m ${s % 60}s"
        else if s < 86400 then s"${s / 3600}h ${(s % 3600) / 60}m"
        else s"${s / 86400}d ${(s % 86400) / 3600}h"

  private def pageSizeToolbar(
      queueName: String,
      sortState: MessageSortState,
      pageSize: PageSize,
      messageCount: Int
  ): Tag =
    div(cls := "toolbar")(
      small(cls := "text-muted")(s"Showing $messageCount messages"),
      div(cls := "toolbar-group")(
        label(cls := "inline-label")("Page size:"),
        tag("select")(
          cls := "inline-select",
          hxGet := messagesTableUrl(queueName, sortState, None, None),
          hxTarget := "#messages-container",
          hxSwap := "innerHTML",
          attr("name") := "pageSize",
          hxInclude := "this"
        )(
          option(value := "10", if pageSizeValue(pageSize) == 10 then selected else frag())("10"),
          option(value := "50", if pageSizeValue(pageSize) == 50 then selected else frag())("50")
        ),
        button(
          cls := "secondary inline-button",
          hxGet := messagesTableUrl(queueName, sortState, Some(pageSize), None),
          hxTarget := "#messages-container",
          hxSwap := "innerHTML"
        )("Refresh")
      )
    )

  private def messagesTable(
      queueName: String,
      messages: List[InspectedMessage],
      sortState: MessageSortState,
      pageSize: PageSize
  ): Tag =
    div(cls := "table-scroll")(
      table(cls := "striped")(
        thead(
          tr(
            sortableHeader("Message ID", MessageSortField.Id, queueName, sortState, pageSize),
            sortableHeader("Read Count", MessageSortField.ReadCount, queueName, sortState, pageSize),
            sortableHeader("Enqueued At", MessageSortField.EnqueuedAt, queueName, sortState, pageSize),
            sortableHeader("Visible At", MessageSortField.VisibleAt, queueName, sortState, pageSize),
            sortableHeader("Last Read At", MessageSortField.LastReadAt, queueName, sortState, pageSize),
            th(cls := "muted")("Headers"),
            th(cls := "muted")("Payload")
          )
        ),
        tbody(
          messages.map: msg =>
            tr(
              td(msg.id.toString),
              td(msg.readCount.toString),
              td(msg.enqueuedAt.toString),
              td(msg.visibleAt.toString),
              td(msg.lastReadAt.fold("N/A")(_.toString)),
              td(msg.headers.getOrElse("N/A")),
              td(pre(code(msg.payload)))
            )
        )
      )
    )

  private def sortableHeader(
      label: String,
      column: MessageSortField,
      queueName: String,
      sortState: MessageSortState,
      pageSize: PageSize
  ): Tag =
    val next = sortState.nextFor(column)
    val arrow =
      if sortState.field == column then
        sortState.direction match
          case SortDirection.Asc  => " \u25B2"
          case SortDirection.Desc => " \u25BC"
      else ""
    val url = messagesTableUrl(queueName, next, Some(pageSize), None)
    th(
      a(
        href := "#",
        hxGet := url,
        hxTarget := "#messages-container",
        hxSwap := "innerHTML"
      )(label + arrow)
    )

  private def paginationControls(
      queueName: String,
      page: CursorPage[InspectedMessage],
      sortState: MessageSortState,
      pageSize: PageSize
  ): Tag =
    div(cls := "pagination")(
      page.prevCursor match
        case Some(cursor) =>
          a(
            href := "#",
            attr("role") := "button",
            cls := "outline",
            hxGet := messagesTableUrl(queueName, sortState, Some(pageSize), Some(cursor.value)),
            hxTarget := "#messages-container",
            hxSwap := "innerHTML"
          )("Previous")
        case None =>
          span()
      ,
      page.nextCursor match
        case Some(cursor) =>
          a(
            href := "#",
            attr("role") := "button",
            cls := "outline",
            hxGet := messagesTableUrl(queueName, sortState, Some(pageSize), Some(cursor.value)),
            hxTarget := "#messages-container",
            hxSwap := "innerHTML"
          )("Next")
        case None =>
          span()
    )

  private def messagesTableUrl(
      queueName: String,
      sortState: MessageSortState,
      pageSize: Option[PageSize],
      cursor: Option[String]
  ): String =
    val params = List(
      pageSize.map(ps => s"pageSize=${pageSizeValue(ps)}"),
      cursor.map(c => s"cursor=$c"),
      Some(s"sortBy=${sortState.field.toString}"),
      Some(s"sortDir=${sortState.direction.toString}")
    ).flatten.mkString("&")
    s"/queues/$queueName/messages/table?$params"

  private def pageSizeValue(pageSize: PageSize): Int =
    pageSize.value

  private def detailTabs(queueName: String, active: String): Frag =
    tags2.nav(cls := "detail-tabs")(
      ul(
        li(if active == "Detail" then strong("Detail") else a(href := s"/queues/$queueName/detail")("Detail")),
        li(
          if active == "Messages" then strong("Messages")
          else a(href := s"/queues/$queueName/messages")("Messages")
        )
      )
    )
