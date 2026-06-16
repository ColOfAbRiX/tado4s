package com.colofabrix.scala.tado4s

import cats.syntax.all.*
import java.nio.file.{ Path, Paths }
import org.http4s.Uri
import pureconfig.*
import scala.concurrent.duration.*

/**
 * Startup configuration of the Tado4s Client
 *
 * @param apiBase Base URL for the API calls
 * @param apiAuth OAuth2 Authentication URL
 * @param apiClientId OAuth2 Client ID
 * @param tokenPath Path to the persisted token file
 * @param httpTimeout HTTP Timeout
 * @param httpRetriesMax Max number of retries for HTTP requests
 * @param httpRetryTimeMax Maximum retry time
 * @param ignoreSsl Setting to disable SSL endpoint verification
 * @param streamingConcurrencyMax Maximum number of concurrent page fetches when streaming
 */
final case class TadoConfig(
  apiBase: Uri,
  apiAuth: Uri,
  apiClientId: String,
  tokenPath: Path,
  httpTimeout: FiniteDuration = 30.seconds,
  httpRetriesMax: Int = 5,
  httpRetryTimeMax: FiniteDuration = 1.minute,
  ignoreSsl: Boolean = false,
  streamingConcurrencyMax: Int = 4,
) derives ConfigReader {

  override def toString: String =
    s"TadoConfig(apiBase=$apiBase, apiAuth=$apiAuth, apiClientId=***, tokenPath=$tokenPath, " +
    s"httpTimeout=$httpTimeout, httpRetriesMax=$httpRetriesMax, httpRetryTimeMax=$httpRetryTimeMax, " +
    s"ignoreSsl=$ignoreSsl, streamingConcurrencyMax=$streamingConcurrencyMax)"

}

/**
 * Tado4s configuration
 */
object TadoConfig {

  val config: Either[TadoConfigError, TadoConfig] =
    ConfigSource
      .default
      .at("tado4s")
      .load[TadoConfig]
      .leftMap { errors =>
        TadoConfigError(errors.toList.map(_.toString).mkString("(", ",", ")"))
      }

  given ConfigReader[FiniteDuration] =
    ConfigReader.fromString:
      ConvertHelpers.optF: str =>
        Some(Duration(str)).collect { case fd: FiniteDuration => fd }

  given ConfigReader[Uri] =
    ConfigReader.fromString:
      ConvertHelpers.tryF: str =>
        Uri.fromString(str).toTry

  given ConfigReader[Path] =
    ConfigReader.fromString:
      ConvertHelpers.optF: str =>
        val expanded = str.replaceFirst("^~", System.getProperty("user.home"))
        Some(Paths.get(expanded))

}
