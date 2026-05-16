package io.github.matejcerny.pgmqadmin.routes

import cats.effect.IO
import io.github.matejcerny.pgmqadmin.endpoints.TopicEndpoints.*
import io.github.matejcerny.pgmqadmin.services.{ QueueService, TopicService }
import io.github.matejcerny.pgmqadmin.views.*
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.Http4sServerInterpreter

object TopicRoutes extends Auth:

  def routes(topicService: TopicService, queueService: QueueService): HttpRoutes[IO] =

    val topicsPageEndpoint =
      secure(topicsPage): _ =>
        (_: Unit) =>
          for
            bindings <- topicService.listBindings()
            (queues, _) <- queueService.listQueues(None, None)
            queueNames = queues.map(_.queueName.toString)
          yield View.fullPage(
            "Topics",
            "Topics",
            TopicViews.topicsContent(bindings, queueNames),
            List("Topics" -> "/topics")
          )

    val topicsTableEndpoint =
      secure(topicsTable): _ =>
        (_: Unit) =>
          topicService
            .listBindings()
            .map:
              TopicViews.bindingsTableHtml(_).render

    val bindTopicRoute =
      secure(bindTopicEndpoint): _ =>
        (pattern: String, queueName: String) =>
          topicService
            .bindTopic(pattern, queueName)
            .flatMap: _ =>
              topicService.listBindings()
            .map:
              TopicViews.bindingsTableHtml(_).render

    val unbindTopicRoute =
      secure(unbindTopicEndpoint): _ =>
        (pattern: String, queueName: String) =>
          topicService
            .unbindTopic(pattern, queueName)
            .flatMap: _ =>
              topicService.listBindings()
            .map:
              TopicViews.bindingsTableHtml(_).render

    val testRoutingRoute =
      secure(testRoutingEndpoint): _ =>
        (routingKey: String) =>
          topicService
            .testRouting(routingKey)
            .map:
              TopicViews.testRoutingResultsHtml(_).render

    Http4sServerInterpreter[IO]().toRoutes(
      List(
        topicsPageEndpoint,
        topicsTableEndpoint,
        bindTopicRoute,
        unbindTopicRoute,
        testRoutingRoute
      )
    )
