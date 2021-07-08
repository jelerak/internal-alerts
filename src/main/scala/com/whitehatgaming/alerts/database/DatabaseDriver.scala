package com.whitehatgaming.alerts.database

import cats.effect.IO
import doobie.Transactor
import doobie.quill.DoobieContext
import io.getquill.{PostgresDialect, SnakeCase}
import io.getquill.context.jdbc.JdbcContext

trait DatabaseDriver {

  val xa: Transactor[IO]

  val ctx: DoobieContext.Postgres[SnakeCase]

  val quillCtx:JdbcContext[PostgresDialect, SnakeCase]

}
