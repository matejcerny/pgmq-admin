package io.github.matejcerny.pgmqadmin.endpoints

import io.github.matejcerny.pgmqadmin.endpoints.AuthEndpoint.{ AuthenticatedEndpoint, authenticated }
import sttp.tapir.*

object TopicEndpoints:

  val topicsPage: AuthenticatedEndpoint[Unit] =
    authenticated.get
      .in("topics")
      .out(htmlBodyUtf8)

  val topicsTable: AuthenticatedEndpoint[Unit] =
    authenticated.get
      .in("topics" / "table")
      .out(htmlBodyUtf8)

  val bindTopicEndpoint: AuthenticatedEndpoint[(String, String)] =
    authenticated.post
      .in("topics" / "bind")
      .in(query[String]("pattern"))
      .in(query[String]("queueName"))
      .out(htmlBodyUtf8)

  val unbindTopicEndpoint: AuthenticatedEndpoint[(String, String)] =
    authenticated.post
      .in("topics" / "unbind")
      .in(query[String]("pattern"))
      .in(query[String]("queueName"))
      .out(htmlBodyUtf8)

  val testRoutingEndpoint: AuthenticatedEndpoint[String] =
    authenticated.get
      .in("topics" / "test-routing")
      .in(query[String]("routingKey"))
      .out(htmlBodyUtf8)
