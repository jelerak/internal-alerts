package com.whitehatgaming.alerts.database

import com.whitehatgaming.alerts.domain.model.AlertMessage.AlertParams
import com.whitehatgaming.alerts.domain.model.AlertStatus
import io.circe.Json
import io.circe.jawn._
import io.getquill.MappedEncoding

import java.time.Instant
import java.util.Date
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

object CommonEncoders {

  implicit val encodeAlertStatus: MappedEncoding[AlertStatus, String] = MappedEncoding[AlertStatus, String](_.entryName)
  implicit val decodeAlertStatus: MappedEncoding[String, AlertStatus] = MappedEncoding[String, AlertStatus](AlertStatus.withNameInsensitive)
  implicit val encodeInstant: MappedEncoding[Instant, Date] = MappedEncoding[Instant, Date](Date.from)
  implicit val decodeInstant: MappedEncoding[Date, Instant] = MappedEncoding[Date, Instant](_.toInstant)
  implicit val encodeDuration: MappedEncoding[Duration, String] = MappedEncoding[Duration, String](_.toString)
  implicit val decodeDuration: MappedEncoding[String, Duration] = MappedEncoding[String, Duration]( d=>
    if (d.equalsIgnoreCase("never")) {
      Duration.Inf
    }
    else if (d.startsWith("P")) {
      // ISO8601 style duration string
      Duration(java.time.Duration.parse(d).getSeconds, TimeUnit.SECONDS)
    }
    else {
      Duration(d)
    }
  )

  implicit val encodeAlertParams: MappedEncoding[AlertParams, String] = MappedEncoding[AlertParams, String]{
    ap => Json.fromJsonObject(ap.orNull).toString()
  }
  implicit val decodeAlertParams: MappedEncoding[String, AlertParams] = MappedEncoding[String, AlertParams] {
    ap => parse(ap) match {
      case Left(value) => throw new RuntimeException(s"could not parse $ap", value)
      case Right(value) => value.asObject
     }
  }
}
