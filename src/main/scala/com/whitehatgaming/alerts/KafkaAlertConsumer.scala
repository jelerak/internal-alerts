package com.whitehatgaming.alerts

import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.effect.{IO, Timer}
import com.whitehatgaming.alerts.Main._
import com.whitehatgaming.alerts.domain.repository.AlertRepository
import com.whitehatgaming.alerts.domain.model.{Alert, AlertMessage}
import fs2.kafka._

import scala.concurrent.duration.DurationInt

class KafkaAlertConsumer(alertRepository: AlertRepository)  {

  implicit def unsafeLogger = Slf4jLogger.getLogger[IO]

  val topicName        = kafkaConsumerConfig.alertTopicName
  val bootstrapServers = kafkaBrokerConfig.brokerList
  val groupId          = "group"

  def run(implicit T: Timer[IO]): fs2.Stream[IO, Unit] = {
    import io.circe.jawn._

    def processRecord(record: ConsumerRecord[String, String]): IO[Unit] = {
      val res = for {
                tmp <- IO.fromEither(parse(record.value))
                alert <- IO.fromEither(tmp.as[AlertMessage])
                _ <- alertRepository.createAlert(Alert(alert))
      } yield()
      res.handleErrorWith(e => Logger[IO].error(e)("could not process kafka message"))
    }

    val consumerSettings =
      ConsumerSettings[IO, String, String]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers(bootstrapServers)
        .withGroupId(groupId)

    KafkaConsumer
      .stream(consumerSettings)
      .evalTap(_.subscribeTo(topicName))
      .flatMap(_.stream)
      .mapAsync(25) { committable =>
        processRecord(committable.record)
          .as(committable.offset)
      }
      .through(commitBatchWithin(500, 15.seconds))

  }
}
