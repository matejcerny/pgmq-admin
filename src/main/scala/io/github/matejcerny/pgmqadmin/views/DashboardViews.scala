package io.github.matejcerny.pgmqadmin.views

import scalatags.Text.all.*

object DashboardViews:

  def dashboardContent: Frag =
    frag(
      h2("Dashboard"),
      p("Work in progress")
    )
