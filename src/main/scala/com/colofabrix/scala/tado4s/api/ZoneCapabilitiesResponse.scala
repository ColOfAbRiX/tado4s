package com.colofabrix.scala.tado4s.api

import io.circe.Decoder

/**
 * Response for zone capabilities
 */
final case class ZoneCapabilitiesResponse(
  `type`: String,
  temperatures: Option[ZoneCapabilitiesResponse.Temperatures],
) derives Decoder

object ZoneCapabilitiesResponse {

  /**
   * Supported temperature ranges in Celsius and Fahrenheit
   */
  final case class Temperatures(
    celsius: TemperatureRange,
    fahrenheit: TemperatureRange,
  ) derives Decoder

  /**
   * Temperature range with minimum, maximum, and step size
   */
  final case class TemperatureRange(
    min: Int,
    max: Int,
    step: Double,
  ) derives Decoder

}
