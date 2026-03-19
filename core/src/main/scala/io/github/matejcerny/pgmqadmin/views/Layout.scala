package io.github.matejcerny.pgmqadmin.views

import scalatags.Text.all.*
import scalatags.Text.tags2

object Layout:

  def fullPage(pageTitle: String, bodyContent: Frag): String =
    val page = html(
      attr("lang") := "en",
      attr("data-theme") := "light",
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1"),
        tag("title")(pageTitle),
        link(
          rel := "stylesheet",
          href := "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css"
        ),
        script(src := "https://unpkg.com/htmx.org@2.0.4")
      ),
      body(
        tags2.nav(cls := "container")(
          ul(li(strong("pgmq-admin"))),
          ul(li("admin"))
        ),
        tag("main")(cls := "container")(
          bodyContent
        )
      )
    )
    "<!DOCTYPE html>\n" + page.render
