package io.github.matejcerny.pgmqadmin.views

import scalatags.Text.all.attr
import scalatags.generic.Attr

object Htmx:

  val hxGet: Attr = attr("hx-get")
  val hxPost: Attr = attr("hx-post")
  val hxDelete: Attr = attr("hx-delete")
  val hxTarget: Attr = attr("hx-target")
  val hxSwap: Attr = attr("hx-swap")
  val hxInclude: Attr = attr("hx-include")
