package com.colofabrix.scala.tado4s

import io.circe.*
import io.circe.derivation.*

/**
 * Base exception class for all Tado4s errors.
 *
 * @param message The error message describing what went wrong
 * @param inner Optional underlying cause of the error
 */
class Tado4sError(message: String, inner: Option[Throwable] = None) extends Throwable(message) {
  inner.foreach(super.addSuppressed)
}

/**
 * Configuration error
 */
class TadoConfigError(message: String) extends Tado4sError(message, None)

/**
 * Error returned by the Tado API.
 */
final case class TadoErrorResponse(errors: List[TadoError])
  extends Tado4sError(errors.mkString("'", ", ", "'"))
  derives Decoder {
  override def toString(): String = errors.mkString("'", ", ", "'")
}

/**
 * Individual Tado API error.
 */
final case class TadoError(code: String, title: String) derives Decoder {
  override def toString(): String = s"$code - $title"
}
