package com.whitehatgaming.alerts.domain.model

import com.whitehatgaming.alerts.domain.model.AlertMessage.AlertParams
import enumeratum._
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.time.{Clock, Instant}

sealed trait AlertStatus extends EnumEntry
case object AlertStatus extends Enum[AlertStatus] with CirceEnum[AlertStatus] {

  val values: IndexedSeq[AlertStatus] = findValues

  case object Sent extends AlertStatus
  case object Pending extends AlertStatus
  case object Error extends AlertStatus
}

object AlertMessage {
  type AlertParams = Option[JsonObject]
  implicit val alertMessageDecoder: Decoder[AlertMessage] = deriveDecoder
  implicit val alertMessageEncoder: Encoder[AlertMessage] = deriveEncoder
  implicit val alertDecoder: Decoder[Alert] = deriveDecoder
  implicit val alertEncoder: Encoder[Alert] = deriveEncoder
}

case class AlertMessage(
  alertType: String,
  userId: Option[Long] = None,
  brandId: Option[Int] = None,
  params: AlertParams = None
)

object Alert{
  import com.whitehatgaming.alerts.domain.model.AlertStatus._
  def apply(msg: AlertMessage): Alert = {
    Alert(None, msg.alertType, msg.userId, msg.brandId, msg.params, Pending)
  }
}
case class Alert(
   id: Option[Long],
   alertType: String,
   userId: Option[Long] = None,
   brandId: Option[Int] = None,
   params: AlertParams = None,
   status: AlertStatus,
   lastUpdate:Instant = Instant.now(Clock.systemUTC())
)