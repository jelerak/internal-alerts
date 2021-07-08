package com.whitehatgaming.alerts.domain.model

case class EmailAddress(
  name: String,
  jurisdiction_id: Option[String],
  address: String
)
