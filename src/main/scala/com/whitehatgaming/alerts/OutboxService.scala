package com.whitehatgaming.alerts

import cats.effect._
import com.whitehatgaming.alerts.domain.repository.{AlertRepository, AlertTypeRepository, EmailAddressRepository}
import com.whitehatgaming.alerts.domain.model.Alert
import com.whitehatgaming.alerts.domain.model.AlertStatus._
import com.whitehatgaming.alerts.domain.model.MailerPayload._
import com.whitehatgaming.alerts.exception.UnknownAlertTypeException
import fs2._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.DurationInt


class OutboxService(alertRepository: AlertRepository, alertTypeRepository: AlertTypeRepository, emailAddressRepository: EmailAddressRepository, mailerClient: MailerClient)  {

  implicit def unsafeLogger = Slf4jLogger.getLogger[IO]

  def pollStream(implicit T: Timer[IO]): Stream[IO, Alert] =
    Stream.evalSeq(alertRepository.findAlertsToSend()).metered(10.seconds)

  def processAlert(alert: Alert): IO[Unit] = {
    val res = for {
      _ <- sendAlert(alert)
      _ <- alertRepository.updateStatus(alert, Sent)
    } yield()
    res.handleErrorWith{ e =>
      for {
        _ <- Logger[IO].error(e)(s"could not send alert ${alert}")
        - <- alertRepository.updateStatus(alert, Error)
      } yield()
    }
  }

  def pollStreamForever(implicit T: Timer[IO]):Stream[IO, Alert] = {
    pollStream(T)
      .evalTap(processAlert)
      .repeat
  }

  def run(implicit T: Timer[IO]): Stream[IO, Alert] = {
     pollStreamForever(T)
      //.metered(20.seconds)
      .take(10)
  }

  def sendAlert(alert: Alert):IO[Unit] = {
    for {
      alertType <- alertTypeRepository.findAlertTypeByName(alert.alertType)
      emailAddress <- if (alertType.isDefined)
        emailAddressRepository.findAddressByNameAndJurisdiction(alertType.get.recipient, alertType.get.jurisdiction_id)
      else IO.raiseError(UnknownAlertTypeException(s"alert type ${alert.alertType} not found"))
      emailRequest = MailerRequest(alertType.get.templateName, MailerAlertParams(emailAddress.get.address, alert.userId, alert.brandId, alert.params))
      _ <- mailerClient.sendAlert(emailRequest)
    } yield()
  }
}
