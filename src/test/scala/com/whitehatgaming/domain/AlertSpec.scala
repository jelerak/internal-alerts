package com.whitehatgaming.domain

import cats.effect.IO
import com.whitehatgaming.alerts.domain.model.AlertMessage
import io.circe.JsonObject
import io.circe.jawn._
import munit.CatsEffectSuite

class AlertSpec extends CatsEffectSuite {

  test("Can parse AlertMessage JSON") {
    val json = """{"alertType":"boh","userId":12, "params":{"a":1, "b": true, "c":"boh"}}"""
    val alert = for {
      tmp <- parse(json)
      alert <- tmp.as[AlertMessage]
    } yield alert
    assertIO(IO.fromEither(alert) , AlertMessage("boh",Some(12), Some(2), Some(JsonObject())))
  }

}