package com.colofabrix.scala.tado4s.api

import io.circe.Decoder

/**
 * Response for air comfort
 */
final case class AirComfortResponse(
  freshness: AirComfortResponse.Freshness,
  comfort: Vector[AirComfortResponse.ZoneComfort],
) derives Decoder

object AirComfortResponse {

  /**
   * Air freshness value and last open window time
   */
  final case class Freshness(
    value: String,
    lastOpenWindow: Option[String],
  ) derives Decoder

  /**
   * Comfort level for a specific zone including temperature and humidity
   */
  final case class ZoneComfort(
    roomId: Int,
    temperatureLevel: String,
    humidityLevel: String,
    coordinate: Coordinate,
  ) derives Decoder

  /**
   * Polar coordinate for comfort visualization
   */
  final case class Coordinate(
    radial: Double,
    angular: Double,
  ) derives Decoder

}
