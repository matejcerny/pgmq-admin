package io.github.matejcerny.pgmqadmin.views

import scalatags.Text.all.*
import scalatags.Text.tags2

object Layout:

  def fullPage(pageTitle: String, bodyContent: Frag): String =
    "<!DOCTYPE html>\n" + html(
      attr("lang") := "en",
      attr("data-theme") := "light",
      pageHead(pageTitle),
      body(
        navigation,
        tag("main")(cls := "container")(bodyContent)
      )
    ).render

  private def pageHead(pageTitle: String): Tag =
    head(
      meta(charset := "utf-8"),
      meta(name := "viewport", content := "width=device-width, initial-scale=1"),
      tag("title")(pageTitle),
      link(
        rel := "stylesheet",
        href := "https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css"
      ),
      script(src := "https://unpkg.com/htmx.org@2.0.4"),
      themeScript
    )

  private val navigation: Tag =
    tags2.nav(cls := "container")(
      ul(li(strong("pgmq-admin"))),
      ul(
        li(
          a(
            href := "#",
            attr("role") := "button",
            cls := "outline secondary",
            style := "padding: 0.25rem 0.5rem;",
            attr("onclick") := "toggleTheme(); return false;"
          )("\uD83C\uDF13")
        ),
        li("admin")
      )
    )

  private val themeScript: Tag =
    script(raw("""
        |var saved = localStorage.getItem('theme');
        |if (saved) {
        |  document.documentElement.setAttribute('data-theme', saved);
        |}
        |
        |function toggleTheme() {
        |  var html = document.documentElement;
        |  var current = html.getAttribute('data-theme');
        |  var next = current === 'dark' ? 'light' : 'dark';
        |  html.setAttribute('data-theme', next);
        |  localStorage.setItem('theme', next);
        |}""".stripMargin))
