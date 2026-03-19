package io.github.matejcerny.pgmqadmin.config

import com.comcast.ip4s.*

case class AppConfig(
    server: ServerConfig,
    db: DbConfig
)

case class ServerConfig(
    host: Host,
    port: Port
)

case class DbConfig(
    host: String,
    port: Int,
    user: String,
    database: String,
    password: Option[String],
    poolSize: Int
)

object AppConfig:
  val default: AppConfig = AppConfig(
    server = ServerConfig(
      host = host"0.0.0.0",
      port = port"8080"
    ),
    db = DbConfig(
      host = "localhost",
      port = 5432,
      user = "postgres",
      database = "postgres",
      password = Some("postgres"),
      poolSize = 10
    )
  )
