package com.whitehatgaming.alerts.common

import cats.effect.{ConcurrentEffect, ContextShift, IO}
import com.typesafe.config.{Config, ConfigFactory}

case class KafkaBrokerConfig(brokerList: String)
case class KafkaConsumersConfig(alertTopicName: String)
case class MailerServiceConfig(baseUri:String, alertsPath:String)
case class DatabaseConfig( url: String, user: String, password: String, flywayMigrationDuringBoot: Boolean)


trait Configuration { self =>

  implicit val cs: ContextShift[IO]
  implicit val ce: ConcurrentEffect[IO]

  lazy val config: Config = ConfigFactory.load()

  lazy val env = if (config.hasPath("env")) config.getString("env") else "test"

  lazy val kafkaBrokerConfig: KafkaBrokerConfig = KafkaBrokerConfig(
    config.getString("kafka.broker.bootstrap"),
  )

  lazy val kafkaConsumerConfig: KafkaConsumersConfig = KafkaConsumersConfig(
    config.getString("kafka.topics.alerts")
  )

  lazy val mailerServiceConfig: MailerServiceConfig = MailerServiceConfig(
    config.getString("mailer.baseUrl"), config.getString("mailer.paths.alerts")
  )

  lazy val dbConfig = DatabaseConfig(
    config.getString("db.flyway.dburl"),
    config.getString("db.ctx.dataSource.user"),
    config.getString("db.ctx.dataSource.password"),
    config.getBoolean("db.flyway.migration-during-boot"))

  lazy val threadPools = new ThreadPools()
}
