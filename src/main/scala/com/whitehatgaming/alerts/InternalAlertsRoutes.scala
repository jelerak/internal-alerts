package com.whitehatgaming.alerts

import cats.effect.IO
import com.whitehatgaming.alerts.domain.repository.AlertRepository
import com.whitehatgaming.alerts.domain.model.{Alert, AlertMessage}
import io.circe.Json
import org.http4s.circe.CirceEntityCodec._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class InternalAlertsRoutes(ar: AlertRepository) {

  def routes(): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._
    import com.whitehatgaming.alerts.domain.model.AlertMessage._
    HttpRoutes.of[IO] {
      case req@POST -> Root / "sendAlert" =>
        req.decode[AlertMessage] { a =>
            ar.createAlert(Alert(a)) flatMap {
              case Some(id) =>
                Created(Json.obj(("id", Json.fromLong(id))))
              case None =>
                InternalServerError()
            }
        }
    }
  }
}
