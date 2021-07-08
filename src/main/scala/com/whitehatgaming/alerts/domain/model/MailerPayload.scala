package com.whitehatgaming.alerts.domain.model

import com.whitehatgaming.alerts.domain.model.AlertMessage.AlertParams

object MailerPayload {

  case class MailerAlertParams(
                                recipient: String,
                                userId: Option[Long],
                                brandId: Option[Int],
                                params: AlertParams
                              )
  case class MailerResponse(status: Boolean, message: Option[String])

  case class MailerRequest(templateName: String, params: MailerAlertParams)

}
