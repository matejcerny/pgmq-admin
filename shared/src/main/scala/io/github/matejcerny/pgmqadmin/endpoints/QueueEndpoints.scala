package io.github.matejcerny.pgmqadmin.endpoints

import io.github.matejcerny.pgmqadmin.endpoints.AuthEndpoint.{ AuthenticatedEndpoint, authenticated }
import sttp.tapir.*

object QueueEndpoints:

  val queuesPage: AuthenticatedEndpoint[Unit] =
    authenticated.get
      .in("queues")
      .out(htmlBodyUtf8)

  val queuesTable: AuthenticatedEndpoint[(Option[String], Option[String])] =
    authenticated.get
      .in("queues" / "table")
      .in(query[Option[String]]("sortBy"))
      .in(query[Option[String]]("sortDir"))
      .out(htmlBodyUtf8)

  val deleteQueue: AuthenticatedEndpoint[String] =
    authenticated.delete
      .in("queues" / path[String]("queueName"))
      .out(htmlBodyUtf8)

  val purgeQueue: AuthenticatedEndpoint[String] =
    authenticated.post
      .in("queues" / path[String]("queueName") / "purge")
      .out(htmlBodyUtf8)

  val queueDetail: AuthenticatedEndpoint[String] =
    authenticated.get
      .in("queues" / path[String]("queueName") / "detail")
      .out(htmlBodyUtf8)

  val queueMessages: AuthenticatedEndpoint[(String, Option[Int])] =
    authenticated.get
      .in("queues" / path[String]("queueName") / "messages")
      .in(query[Option[Int]]("qty"))
      .out(htmlBodyUtf8)

  val queueSettings: AuthenticatedEndpoint[String] =
    authenticated.get
      .in("queues" / path[String]("queueName") / "settings")
      .out(htmlBodyUtf8)

  val createQueue: AuthenticatedEndpoint[String] =
    authenticated.post
      .in("queues" / path[String]("queueName"))
      .out(htmlBodyUtf8)
