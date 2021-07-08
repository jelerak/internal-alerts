package com.whitehatgaming.alerts

import cats.effect._
import com.whitehatgaming.alerts.Main._
import com.whitehatgaming.alerts.domain.model.MailerPayload._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.blaze.BlazeClientBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class MailerClient() {
  import io.circe.generic.auto._
  import threadPools._
  implicit def unsafeLogger = Slf4jLogger.getLogger[IO]

  case class MailerError(e: Throwable) extends RuntimeException

  val mailerUri: Uri = Uri.unsafeFromString(s"${mailerServiceConfig.baseUri}${mailerServiceConfig.alertsPath}")

  def sendAlert(alert: MailerRequest): IO[Unit] = {

    val req = IO.pure(Request(Method.POST, mailerUri.addSegment(alert.templateName)).withEntity(alert.params)(jsonEncoderOf[IO, MailerAlertParams]))
    BlazeClientBuilder[IO](otherIOEc).stream
      .flatMap { httpClient =>
        fs2.Stream.eval(httpClient.expect[MailerResponse](req)(jsonOf[IO, MailerResponse]))
      }
      .compile
      .drain
      .handleErrorWith(e => Logger[IO].error(e)(s"could not send alert ${alert}"))
  }
}
