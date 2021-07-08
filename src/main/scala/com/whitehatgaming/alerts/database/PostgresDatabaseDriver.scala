package com.whitehatgaming.alerts.database

import cats.effect.{Blocker, ContextShift, IO}
import com.whitehatgaming.alerts.common.ThreadPools
import doobie.Transactor
import doobie.quill.DoobieContext
import io.getquill.{PostgresJdbcContext, SnakeCase}

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

class PostgresDatabaseDriver(threadPools: ThreadPools)(implicit cs: ContextShift[IO]) extends DatabaseDriver {

  override lazy val quillCtx = new PostgresJdbcContext(SnakeCase, "db.ctx")

  private val blocker: Blocker    = Blocker.liftExecutorService(threadPools.jdbc)
  private val connectionAwaitPool = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

  override lazy val xa: Transactor[IO]= Transactor.fromDataSource[IO](quillCtx.dataSource, connectionAwaitPool, blocker)

  override lazy val ctx: DoobieContext.Postgres[SnakeCase] = new DoobieContext.Postgres[SnakeCase](SnakeCase)

}
