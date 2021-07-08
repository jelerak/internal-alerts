package com.whitehatgaming.alerts.domain.repository

import cats.effect.IO
import com.whitehatgaming.alerts.database.DatabaseDriver
import com.whitehatgaming.alerts.domain.model.EmailAddress
import doobie.implicits._
import io.getquill.Ord

class EmailAddressRepository(postgresDriver: DatabaseDriver) {
  import postgresDriver._
  import ctx._

  def findAll(): IO[List[EmailAddress]] =
    ctx.run(query[EmailAddress]).transact(xa)

  def findAddressByNameAndJurisdiction(name: String, jurisdiction:Option[String] = None): IO[Option[EmailAddress]] = {
    ctx.run(query[EmailAddress]
      .filter(a => a.name == lift(name) && (a.jurisdiction_id == lift(jurisdiction) || a.jurisdiction_id.isEmpty))
      .sortBy(_.jurisdiction_id)(Ord.descNullsLast)).map(_.headOption)
      .transact(xa)
  }
}
