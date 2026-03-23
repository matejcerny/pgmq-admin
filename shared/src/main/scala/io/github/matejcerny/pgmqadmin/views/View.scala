package io.github.matejcerny.pgmqadmin.views

import io.github.matejcerny.pgmqadmin.config.BuildInfo
import scalatags.Text.all.*
import scalatags.Text.tags2

object View:

  extension (b: Boolean) def render: String = if b then "Yes" else "No"

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

  def fullPage(activeNav: String, pageTitle: String, bodyContent: Frag): String =
    "<!DOCTYPE html>\n" + html(
      attr("lang") := "en",
      attr("data-theme") := "light",
      pageHead(pageTitle),
      body(
        navigation(activeNav),
        tag("main")(cls := "container")(bodyContent)
      )
    ).render

  private def navigation(activeNav: String): Tag =
    tags2.nav(cls := "container")(
      ul(
        li(strong(a(href := "/")("pgmq-admin"))),
        li(navLink("Dashboard", "/", activeNav)),
        li(navLink("Queues", "/queues", activeNav)),
        li(navLink("Topics", "/topics", activeNav)),
        li(navLink("Metrics", "/metrics", activeNav))
      ),
      ul(
        li(small(s"v${BuildInfo.version}")),
        li(
          a(
            href := "#",
            attr("role") := "button",
            cls := "outline secondary small",
            attr("onclick") := "toggleTheme(); return false;"
          )("\uD83C\uDF13")
        )
      )
    )

  private def navLink(label: String, href_ : String, activeNav: String): Tag =
    if label == activeNav then a(href := href_, attr("aria-current") := "page")(label)
    else a(href := href_)(label)

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
