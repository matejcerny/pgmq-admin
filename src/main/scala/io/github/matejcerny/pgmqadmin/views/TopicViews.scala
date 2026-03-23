package io.github.matejcerny.pgmqadmin.views

import scalatags.Text.all.*

object TopicViews:

  def topicsContent: Frag =
    frag(
      h2("Topics"),
      p("Work in progress")
    )
