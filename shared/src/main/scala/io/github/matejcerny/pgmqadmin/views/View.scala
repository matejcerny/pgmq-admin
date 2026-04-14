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
      link(
        rel := "stylesheet",
        href := "/css/app.css"
      ),
      script(src := "https://unpkg.com/htmx.org@2.0.4"),
      script(src := "/js/app.js")
    )

  def fullPage(
      activeNav: String,
      pageTitle: String,
      bodyContent: Frag,
      breadcrumb: List[(String, String)] = Nil
  ): String =
    "<!DOCTYPE html>\n" + html(
      attr("lang") := "en",
      attr("data-theme") := "light",
      pageHead(pageTitle),
      body(
        cls := "layout",
        headerBar(breadcrumb),
        sidebar(activeNav),
        div(cls := "main-content")(
          tag("main")(cls := "container")(bodyContent)
        ),
        script(raw("initSidebar();"))
      )
    ).render

  private def headerBar(breadcrumb: List[(String, String)]): Tag =
    tag("header")(cls := "top-bar")(
      div(cls := "top-bar-left")(
        a(
          cls := "sidebar-toggle",
          href := "#",
          attr("aria-label") := "Toggle menu",
          attr("onclick") := "toggleSidebar(); return false;"
        )("\u2630"),
        tag("nav")(cls := "header-breadcrumb", attr("aria-label") := "breadcrumb")(
          ul(
            li(strong(a(href := "/")("pgmq-admin"))),
            breadcrumb
              .dropRight(1)
              .map: (label, path) =>
                li(a(href := path)(label)),
            breadcrumb.lastOption.map: (label, _) =>
              li(label)
          )
        )
      ),
      div(cls := "top-bar-right")(
        small(s"v${BuildInfo.version}"),
        a(
          href := "#",
          attr("role") := "button",
          cls := "outline secondary",
          attr("onclick") := "toggleTheme(); return false;"
        )("\uD83C\uDF13")
      )
    )

  private def sidebar(activeNav: String): Tag =
    tag("aside")(cls := "sidebar", id := "sidebar")(
      tags2.nav(
        ul(
          li(sidebarLink("Dashboard", "/", activeNav)),
          li(sidebarLink("Queues", "/queues", activeNav)),
          li(sidebarLink("Topics", "/topics", activeNav)),
          li(sidebarLink("Metrics", "/metrics", activeNav))
        )
      )
    )

  private def sidebarLink(label: String, href_ : String, activeNav: String): Tag =
    if label == activeNav then a(href := href_, cls := "active")(label)
    else a(href := href_)(label)
