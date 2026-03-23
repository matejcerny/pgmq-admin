package io.github.matejcerny.pgmqadmin.views

import io.github.matejcerny.pgmqadmin.views.Htmx.*
import pgmq4s.QueueInfo
import scalatags.Text.all.*
import scalatags.Text.tags2

object QueueViews:

  def queuesContent(queues: List[QueueInfo]): Frag =
    frag(
      div(
        style := "display: flex; justify-content: space-between; align-items: center;",
        h2("Queues"),
        div(
          button(
            attr("onclick") := "document.getElementById('create-queue-modal').showModal()"
          )("Create Queue"),
          raw("&nbsp;"),
          button(
            cls := "secondary",
            hxGet := "/queues/table",
            hxTarget := "#queue-table-container",
            hxSwap := "innerHTML"
          )("Refresh")
        )
      ),
      div(id := "queue-table-container")(
        queuesTableHtml(queues)
      ),
      createQueueModal,
      queues.map(qi => deleteModal(qi.queueName.toString)),
      queues.map(qi => purgeModal(qi.queueName.toString))
    )

  def queuesTableHtml(queues: List[QueueInfo]): Tag =
    table(role := "grid")(
      thead(
        tr(
          th("Queue Name"),
          th("Created At"),
          th("Actions")
        )
      ),
      tbody(
        queues.map: qi =>
          val name: String = qi.queueName.toString
          tr(
            td(name),
            td(qi.createdAt.toString),
            td(
              a(
                href := "#",
                attr("role") := "button",
                cls := "outline secondary",
                style := "padding: 0.25rem 0.5rem; font-size: 1.2rem;",
                attr("onclick") := s"document.getElementById('purge-modal-$name').showModal(); return false;",
                attr("title") := "Purge messages"
              )("\uD83E\uDDF9"),
              raw("&nbsp;"),
              a(
                href := "#",
                attr("role") := "button",
                cls := "outline secondary",
                style := "padding: 0.25rem 0.5rem; font-size: 1.2rem;",
                attr("onclick") := s"document.getElementById('delete-modal-$name').showModal(); return false;",
                attr("title") := "Delete queue"
              )("\uD83D\uDDD1")
            )
          )
      )
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
