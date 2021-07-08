package com.whitehatgaming.alerts.common

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.ExecutionContext

class ThreadPools {

  implicit val jdbc: ExecutorService     = Executors.newCachedThreadPool()
  implicit val jdbcEc: ExecutionContext  = ExecutionContext.fromExecutor(jdbc)
  implicit val otherIO: ExecutorService  = Executors.newFixedThreadPool(32)
  implicit val otherIOEc: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))

}
