package com.whitehatgaming.alerts.domain.model

import scala.concurrent.duration.Duration

case class AlertType(
  name: String,
  recipient: String,
  templateName: String,
  jurisdiction_id: Option[String] = None,
  maxFrequency: Duration
)
