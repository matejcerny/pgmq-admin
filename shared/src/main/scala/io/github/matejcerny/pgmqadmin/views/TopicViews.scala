package io.github.matejcerny.pgmqadmin.views

import io.github.matejcerny.pgmqadmin.views.Htmx.*
import io.github.matejcerny.pgmqadmin.views.View.render
import pgmq4s.domain.RoutingMatch
import scalatags.Text.all.*
import scalatags.Text.tags2

object TopicViews:

  def topicsContent(bindings: List[RoutingMatch], queueNames: List[String]): Frag =
    frag(
      tags2.nav(
        ul(li(h2("Topics"))),
        ul(
          li(
            button(
              attr("onclick") := "document.getElementById('create-binding-modal').showModal()"
            )("Create Binding")
          ),
          li(
            button(
              cls := "outline secondary",
              attr("onclick") := "document.getElementById('test-routing-modal').showModal()"
            )("Test Routing")
          ),
          li(
            button(
              cls := "secondary",
              hxGet := "/topics/table",
              hxTarget := "#bindings-table-container",
              hxSwap := "innerHTML"
            )("Refresh")
          )
        )
      ),
      div(id := "bindings-table-container")(
        bindingsTableHtml(bindings)
      ),
      createBindingModal(queueNames),
      testRoutingModal(),
      bindings.map(binding => unbindModal(binding.pattern.toString, binding.queueName.toString))
    )

  def bindingsTableHtml(bindings: List[RoutingMatch]): Tag =
    table(cls := "striped")(
      thead(
        tr(
          th(cls := "muted")("Pattern"),
          th(cls := "muted")("Queue Name"),
          th(cls := "muted")("Actions")
        )
      ),
      tbody(
        if bindings.isEmpty then tr(td(colspan := "3", cls := "muted")("No bindings found"))
        else
          bindings.map: binding =>
            val pattern = binding.pattern.toString
            val queueName = binding.queueName.toString
            tr(
              td(pattern),
              td(a(href := s"/queues/$queueName/detail")(queueName)),
              td(
                a(
                  href := "#",
                  attr("role") := "button",
                  cls := "outline secondary",
                  attr("onclick") :=
                    s"document.getElementById('unbind-modal-$pattern-$queueName').showModal(); return false;",
                  attr("title") := "Unbind"
                )("\uD83D\uDDD1")
              )
            )
      )
    )

  private def testRoutingModal(): Tag =
    tag("dialog")(id := "test-routing-modal")(
      tags2.article(
        tag("header")(
          a(
            href := "#",
            attr("aria-label") := "Close",
            attr("rel") := "prev",
            attr("onclick") := "this.closest('dialog').close(); return false;"
          ),
          strong("Test Routing")
        ),
        p(
          label(
            "Routing Key",
            tag("fieldset")(attr("role") := "group")(
              input(
                tpe := "text",
                id := "test-routing-key",
                name := "routingKey",
                placeholder := "orders.created"
              ),
              button(
                attr("onclick") := "testRouting();"
              )("Test")
            )
          )
        ),
        div(id := "test-routing-results"),
        tag("footer")(
          button(
            cls := "secondary",
            attr("onclick") := "this.closest('dialog').close();"
          )("Close")
        )
      )
    )

  def testRoutingResultsHtml(results: List[RoutingMatch]): Tag =
    if results.isEmpty then p(cls := "muted")("No matching queues found")
    else
      table(cls := "striped")(
        thead(
          tr(
            th(cls := "muted")("Pattern"),
            th(cls := "muted")("Queue Name"),
            th(cls := "muted")("Compiled Regex")
          )
        ),
        tbody(
          results.map: result =>
            tr(
              td(result.pattern.toString),
              td(result.queueName.toString),
              td(code(result.compiledRegex))
            )
        )
      )

  private def createBindingModal(queueNames: List[String]): Tag =
    tag("dialog")(id := "create-binding-modal")(
      tags2.article(
        tag("header")(
          a(
            href := "#",
            attr("aria-label") := "Close",
            attr("rel") := "prev",
            attr("onclick") := "this.closest('dialog').close(); return false;"
          ),
          strong("Create Binding")
        ),
        p(
          label(
            "Pattern",
            input(
              tpe := "text",
              id := "create-binding-pattern",
              name := "pattern",
              placeholder := "orders.*",
              attr("required") := "true"
            )
          ),
          label(
            "Queue Name",
            select(id := "create-binding-queue-name", name := "queueName", attr("required") := "true")(
              option(value := "", disabled, selected)("Select a queue"),
              queueNames.map(name => option(value := name)(name))
            )
          )
        ),
        tag("footer")(
          button(
            cls := "secondary",
            attr("onclick") := "this.closest('dialog').close();"
          )("Cancel"),
          button(
            attr("onclick") := "createBinding.call(this);"
          )("Create")
        )
      )
    )

  private def unbindModal(pattern: String, queueName: String): Tag =
    tag("dialog")(id := s"unbind-modal-$pattern-$queueName")(
      tags2.article(
        tag("header")(
          a(
            href := "#",
            attr("aria-label") := "Close",
            attr("rel") := "prev",
            attr("onclick") := "this.closest('dialog').close(); return false;"
          ),
          strong("Unbind Topic")
        ),
        p(s"""Are you sure you want to unbind pattern "$pattern" from queue "$queueName"?"""),
        tag("footer")(
          button(
            cls := "secondary",
            attr("onclick") := "this.closest('dialog').close();"
          )("Cancel"),
          button(
            hxPost := s"/topics/unbind?pattern=${pattern}&queueName=${queueName}",
            hxTarget := "#bindings-table-container",
            hxSwap := "innerHTML",
            attr("onclick") := "this.closest('dialog').close();"
          )("Unbind")
        )
      )
    )
