package io.github.matejcerny.pgmqadmin.domain

final case class User(name: String, role: String)

object User:
  val admin: User = User("admin", "admin")
