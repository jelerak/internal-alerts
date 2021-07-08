package com.whitehatgaming.alerts.common

import com.softwaremill.macwire.wire
import com.whitehatgaming.alerts.{InternalAlertsRoutes, InternalAlertsServer, KafkaAlertConsumer, MailerClient, OutboxService}


trait ServiceModule  { self:PersistenceModule =>
  lazy val internalAlertsServer: InternalAlertsServer = wire[InternalAlertsServer]
  lazy val kafkaAlertConsumer: KafkaAlertConsumer = wire[KafkaAlertConsumer]
  lazy val internalAlertsRoutes: InternalAlertsRoutes = wire[InternalAlertsRoutes]
  lazy val outboxService:OutboxService = wire[OutboxService]
  lazy val mailerClient:MailerClient = wire[MailerClient]
}
