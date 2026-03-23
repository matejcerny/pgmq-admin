package io.github.matejcerny.pgmqadmin.views

import io.github.matejcerny.pgmqadmin.model.{ SortColumn, SortDir, SortState }
import io.github.matejcerny.pgmqadmin.views.Htmx.*
import io.github.matejcerny.pgmqadmin.views.View.render
import pgmq4s.QueueInfo
import scalatags.Text.all.*
import scalatags.Text.tags2

object QueueViews:

  def queuesContent(queues: List[QueueInfo]): Frag =
    frag(
      tags2.nav(
        ul(li(h2("Queues"))),
        ul(
          li(
            button(
              attr("onclick") := "document.getElementById('create-queue-modal').showModal()"
            )("Create Queue")
          ),
          li(
            button(
              cls := "secondary",
              hxGet := "/queues/table",
              hxTarget := "#queue-table-container",
              hxSwap := "innerHTML"
            )("Refresh")
          )
        )
      ),
      div(id := "queue-table-container")(
        queuesTableHtml(queues)
      ),
      createQueueModal,
      queues.map(qi => deleteModal(qi.queueName.toString)),
      queues.map(qi => purgeModal(qi.queueName.toString))
    )

  def queuesTableHtml(queues: List[QueueInfo], sort: Option[SortState] = None): Tag =
    table(cls := "striped")(
      thead(
        tr(
          sortableHeader("Queue Name", SortColumn.Name, sort),
          th("Partitioned"),
          th("Unlogged"),
          sortableHeader("Created At", SortColumn.CreatedAt, sort),
          th("Actions")
        )
      ),
      tbody(
        queues.map: qi =>
          val name: String = qi.queueName.toString
          tr(
            td(a(href := s"/queues/$name/detail")(name)),
            td(qi.isPartitioned.render),
            td(qi.isUnlogged.render),
            td(qi.createdAt.toString),
            td(
              a(
                href := "#",
                attr("role") := "button",
                cls := "outline secondary",
                attr("onclick") := s"document.getElementById('purge-modal-$name').showModal(); return false;",
                attr("title") := "Purge messages"
              )("\uD83E\uDDF9"),
              raw(" "),
              a(
                href := "#",
                attr("role") := "button",
                cls := "outline secondary",
                attr("onclick") := s"document.getElementById('delete-modal-$name').showModal(); return false;",
                attr("title") := "Delete queue"
              )("\uD83D\uDDD1")
            )
          )
      )
    )

  private def sortableHeader(label: String, column: SortColumn, sort: Option[SortState]): Tag =
    val next: Option[SortState] = sort match
      case Some(s) => s.nextFor(column)
      case None    => Some(SortState.firstFor(column))
    val arrow: String = sort match
      case Some(SortState(`column`, SortDir.Asc))  => " \u25B2"
      case Some(SortState(`column`, SortDir.Desc)) => " \u25BC"
      case _                                       => ""
    val url: String = next match
      case Some(s) => s"/queues/table?sortBy=${s.column.value}&sortDir=${s.dir.value}"
      case None    => "/queues/table"
    th(
      a(
        href := "#",
        attr("role") := "button",
        cls := "outline secondary",
        hxGet := url,
        hxTarget := "#queue-table-container",
        hxSwap := "innerHTML"
      )(label + arrow)
    )

  private def createQueueModal: Tag =
    tag("dialog")(id := "create-queue-modal")(
      tags2.article(
        tag("header")(
          a(
            href := "#",
            attr("aria-label") := "Close",
            attr("rel") := "prev",
            attr("onclick") := "this.closest('dialog').close(); return false;"
          ),
          strong("Create Queue")
        ),
        p(
          label(
            "Queue Name",
            input(
              tpe := "text",
              id := "create-queue-name",
              name := "queueName",
              placeholder := "my-queue",
              attr("required") := "true"
            )
          )
        ),
        tag("footer")(
          button(
            cls := "secondary",
            attr("onclick") := "this.closest('dialog').close();"
          )("Cancel"),
          button(
            attr("onclick") :=
              """var name = document.getElementById('create-queue-name').value.trim();
                |if (name) {
                |  htmx.ajax('POST', '/queues/' + encodeURIComponent(name), {target: '#queue-table-container', swap: 'innerHTML'});
                |  this.closest('dialog').close();
                |  document.getElementById('create-queue-name').value = '';
                |}""".stripMargin
          )("Create")
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
        p(s"""Are you sure you want to delete queue "$queueName"?"""),
        tag("footer")(
          button(
            cls := "secondary",
            attr("onclick") := "this.closest('dialog').close();"
          )("Cancel"),
          button(
            hxDelete := s"/queues/$queueName",
            hxTarget := "#queue-table-container",
            hxSwap := "innerHTML",
            attr("onclick") := "this.closest('dialog').close();"
          )("Delete")
        )
      )
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
            hxPost := s"/queues/$queueName/purge",
            hxTarget := "#queue-table-container",
            hxSwap := "innerHTML",
            attr("onclick") := "this.closest('dialog').close();"
          )("Purge")
        )
      )
    )
