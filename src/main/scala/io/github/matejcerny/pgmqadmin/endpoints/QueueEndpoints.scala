package io.github.matejcerny.pgmqadmin.endpoints

import io.github.matejcerny.pgmqadmin.endpoints.AuthEndpoint.{ AuthenticatedEndpoint, authenticated }
import sttp.tapir.*

object QueueEndpoints:

  val queuesPage: AuthenticatedEndpoint[Unit] =
    authenticated.get
      .in("queues")
      .out(htmlBodyUtf8)

  val queuesTable: AuthenticatedEndpoint[Unit] =
    authenticated.get
      .in("queues" / "table")
      .out(htmlBodyUtf8)

  val deleteQueue: AuthenticatedEndpoint[String] =
    authenticated.delete
      .in("queues" / path[String]("queueName"))
      .out(htmlBodyUtf8)

  val purgeQueue: AuthenticatedEndpoint[String] =
    authenticated.post
      .in("queues" / path[String]("queueName") / "purge")
      .out(htmlBodyUtf8)

  val createQueue: AuthenticatedEndpoint[String] =
    authenticated.post
      .in("queues" / path[String]("queueName"))
      .out(htmlBodyUtf8)
