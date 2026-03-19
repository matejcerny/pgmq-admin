package io.github.matejcerny.pgmqadmin.auth

final case class User(name: String, role: String)

object User:
  val admin: User = User("admin", "admin")
