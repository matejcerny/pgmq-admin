package io.github.matejcerny.pgmqadmin.model

enum SortColumn(val value: String):
  case Name extends SortColumn("name")
  case CreatedAt extends SortColumn("createdAt")

object SortColumn:
  def fromString(s: String): Option[SortColumn] =
    SortColumn.values.find(_.value == s)

enum SortDir(val value: String):
  case Asc extends SortDir("asc")
  case Desc extends SortDir("desc")

object SortDir:
  def fromString(s: String): Option[SortDir] =
    SortDir.values.find(_.value == s)

case class SortState(column: SortColumn, dir: SortDir):
  def nextFor(col: SortColumn): Option[SortState] =
    if column == col then
      dir match
        case SortDir.Asc  => Some(SortState(col, SortDir.Desc))
        case SortDir.Desc => None
    else Some(SortState(col, SortDir.Asc))

object SortState:
  def from(sortBy: Option[String], sortDir: Option[String]): Option[SortState] =
    for
      col <- sortBy.flatMap(SortColumn.fromString)
      dir <- sortDir.flatMap(SortDir.fromString)
    yield SortState(col, dir)

  def firstFor(col: SortColumn): SortState =
    SortState(col, SortDir.Asc)
