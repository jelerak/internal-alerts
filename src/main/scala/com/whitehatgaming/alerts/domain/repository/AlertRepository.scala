package com.whitehatgaming.alerts.domain.repository

import cats.effect._
import cats.implicits.catsSyntaxApplicativeId
import com.whitehatgaming.alerts.database.{CommonEncoders, DatabaseDriver}
import com.whitehatgaming.alerts.domain.model.AlertStatus._
import com.whitehatgaming.alerts.domain.model.{Alert, AlertStatus, AlertType}
import doobie.implicits._
import doobie.ConnectionIO

import java.time.{Clock, Instant}

class AlertRepository(postgresDriver: DatabaseDriver) {
  import postgresDriver._
  import CommonEncoders._
  import ctx._


  private implicit class InstantQuotes(left: Instant) {
    def > (right: Instant) = quote(infix"$left > $right".as[Boolean])

    def < (right: Instant) = quote(infix"$left < $right".as[Boolean])
  }


  def createAlert(alert: Alert): IO[Option[Long]] = {
    for {
      alertType <- ctx.run(query[AlertType].filter(at => at.name == lift(alert.alertType))).map(_.headOption)
      maxGap = alertType.map(at => Instant.now(Clock.systemUTC()).minusMillis(at.maxFrequency.toMillis)).getOrElse(Instant.now())
      a <- ctx.run(query[Alert]
        .filter(a => a.alertType == lift(alert.alertType) && a.userId.forall(_ == lift(alert.userId.getOrElse(-1L))) && a.lastUpdate > lift(maxGap)))
        .map(_.headOption)
      res <- if (a.isEmpty) ctx.run(query[Alert].insert(lift(alert)).returningGenerated(_.id)) else (Option(0L)).pure[ConnectionIO]
    } yield(res)
  }.transact(xa)

  def findAlertsToSend():IO[List[Alert]] = {
    ctx.run(query[Alert].filter(_.status == lift(Pending:AlertStatus))).transact(xa)
  }

  def updateStatus(alert: Alert, status: AlertStatus): IO[Unit] = {
    for {
      _ <-  ctx.run(query[Alert].filter(_.id == lift(alert.id)).update(_.status -> lift(status:AlertStatus)))
    } yield ()
  }.transact(xa)
}
