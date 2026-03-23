package io.github.matejcerny.pgmqadmin.views

import scalatags.Text.all.*

object MetricViews:

  def metricsContent: Frag =
    frag(
      h2("Metrics"),
      p("Work in progress")
    )
