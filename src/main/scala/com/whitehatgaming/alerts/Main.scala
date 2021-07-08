package com.whitehatgaming.alerts

import cats.effect._
import com.whitehatgaming.alerts.common.{Configuration, PersistenceModule, ServiceModule}
import org.flywaydb.core.Flyway
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

trait Setup extends  ServiceModule
  with PersistenceModule
  with Configuration

object Main extends IOApp with Setup {
  implicit def unsafeLogger = Slf4jLogger.getLogger[IO]
  override implicit val cs: ContextShift[IO]       = IO.contextShift(threadPools.otherIOEc)
  override implicit val ce: ConcurrentEffect[IO]   = IO.ioConcurrentEffect(cs)

  def run(args: List[String]) = {

    // Apply database migration
    if (dbConfig.flywayMigrationDuringBoot) {
      val flyway = Flyway.configure()
        .schemas("internal_alerts")
        .baselineOnMigrate(true)
        .baselineVersion("0")
        .ignoreMissingMigrations(true)
        .locations("db/migrations/all", s"db/migrations/$env")
        .outOfOrder(true)
        .dataSource(dbConfig.url, dbConfig.user, dbConfig.password).load()
      flyway.migrate()
    }
    internalAlertsServer.stream
      .concurrently(kafkaAlertConsumer.run)
      .concurrently(outboxService.run)
      .compile.drain.as(ExitCode.Success)
  }.handleErrorWith{
    e => Logger[IO].error(e)("server error")
    IO.pure(ExitCode.Error)
  }
}
