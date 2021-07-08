package com.whitehatgaming.alerts.exception

import java.util.UUID


abstract class ApplicationException(msg: String) extends RuntimeException(msg) {
  val errorCode: Int
  val errorId: String = UUID.randomUUID().toString
}

case class UnknownAlertTypeException(msg: String) extends ApplicationException(msg) {
  override val errorCode: Int = 100
  def this(msg: String, t: Throwable) = { this(msg); initCause(t) }
}
