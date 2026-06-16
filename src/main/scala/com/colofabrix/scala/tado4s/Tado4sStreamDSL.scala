package com.colofabrix.scala.tado4s

import cats.effect.Concurrent
import com.colofabrix.scala.tado4s.api.*
import fs2.Stream
import java.time.LocalDate

/**
 * Streaming DSL for Tado4s client.
 *
 * Provides fs2 streaming over day report data, which is the primary use case for streaming
 * since a day report covers only a single day and users typically want data across date ranges.
 */
trait Tado4sStreamDSL {

  extension [F[_]: Concurrent](client: Tado4sClient[F])

    /**
     * Stream day reports for a zone across a date range with concurrency.
     *
     * Fetches multiple days in parallel up to the configured `streamingConcurrencyMax`.
     *
     * @param homeId The home ID
     * @param zoneId The zone ID
     * @param from Start date (inclusive)
     * @param to End date (inclusive)
     */
    def streamZoneDayReports(
      homeId: Int,
      zoneId: Int,
      from: LocalDate,
      to: LocalDate,
    ): Stream[F, DayReportResponse] =
      Stream
        .emits {
          from.toEpochDay.to(to.toEpochDay)
        }
        .map(LocalDate.ofEpochDay)
        .parEvalMap(client.config.streamingConcurrencyMax) { date =>
          client.getZoneDayReport(homeId, zoneId, date)
        }

}

object Tado4sStreamDSL extends Tado4sStreamDSL
