package com.whitehatgaming.alerts.domain.repository

import cats.effect._
import com.whitehatgaming.alerts.database._
import com.whitehatgaming.alerts.domain.model.AlertType
import com.whitehatgaming.alerts.exception.UnknownAlertTypeException
import doobie.implicits._

class AlertTypeRepository(postgresDriver: DatabaseDriver) {
  import postgresDriver._
  import ctx._
  import CommonEncoders._

  def raiseErrorIfNotExists(alertType: String): IO[AlertType] = {
    ctx.run(query[AlertType].filter(at => at.name == lift(alertType))).map(_.headOption).transact(xa)
      .flatMap {
        case None => IO.raiseError(UnknownAlertTypeException(s"Alert of type = $alertType does not exist"))
        case Some(value) => IO.pure(value)
      }
  }

  def findAlertTypeByName(name:String):IO[Option[AlertType]] = {
    ctx.run(query[AlertType].filter(_.name == lift(name))).map(_.headOption).transact(xa)
  }
}
