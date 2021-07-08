package com.whitehatgaming.alerts.common

import com.whitehatgaming.alerts.database.{DatabaseDriver, PostgresDatabaseDriver}
import com.whitehatgaming.alerts.domain.repository.{AlertRepository, AlertTypeRepository, EmailAddressRepository}
import com.softwaremill.macwire.wire


trait PersistenceModule { self:Configuration =>

  lazy val postgresDriver: DatabaseDriver = wire[PostgresDatabaseDriver]

  lazy val alertRepository: AlertRepository                          = wire[AlertRepository]
  lazy val emailAddressRepository : EmailAddressRepository           = wire[EmailAddressRepository]
  lazy val alertTypeRepository: AlertTypeRepository                  = wire[AlertTypeRepository]

}
