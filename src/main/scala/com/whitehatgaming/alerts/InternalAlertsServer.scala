package com.whitehatgaming.alerts

import cats.effect.{ConcurrentEffect, ExitCode, IO, Timer}
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

class InternalAlertsServer(internalAlertsRoutes: InternalAlertsRoutes) {

  def stream(implicit T: Timer[IO], concurrentEffect: ConcurrentEffect[IO]): Stream[IO, ExitCode] = {

    val httpApp = (
      internalAlertsRoutes.routes()
    ).orNotFound

    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(finalHttpApp)
      .serve
  }
}
