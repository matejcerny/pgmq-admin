package io.github.matejcerny.pgmqadmin.domain

import pgmq4s.domain.pagination.{ MessageSortField, Sort, SortDirection }

case class MessageSortState(field: MessageSortField, direction: SortDirection):
  def nextFor(col: MessageSortField): MessageSortState =
    direction match
      case SortDirection.Asc if field == col  => MessageSortState(col, SortDirection.Desc)
      case SortDirection.Desc if field == col => MessageSortState(col, SortDirection.Asc)
      case _                                  => MessageSortState(col, SortDirection.Asc)

  def toSort: Sort[MessageSortField] = Sort(field, direction)

object MessageSortState:
  val default: MessageSortState = MessageSortState(MessageSortField.Id, SortDirection.Desc)

  def from(sortBy: Option[String], sortDir: Option[String]): MessageSortState =
    val parsed: Option[MessageSortState] =
      for
        field <- sortBy.flatMap(MessageSortField.fromName)
        dir <- sortDir.flatMap(SortDirection.fromName)
      yield MessageSortState(field, dir)
    parsed.getOrElse(default)
