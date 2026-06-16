package com.colofabrix.scala.tado4s.api

import io.circe.Decoder
import java.time.*

/**
 * Current weather conditions at the home location including temperature and solar intensity
 */
final case class WeatherResponse(
  solarIntensity: WeatherResponse.SolarIntensity,
  outsideTemperature: WeatherResponse.OutsideTemperature,
  weatherState: WeatherResponse.WeatherState,
) derives Decoder

object WeatherResponse {

  /**
   * Outside temperature reading with precision and timestamp
   */
  final case class OutsideTemperature(
    celsius: Double,
    fahrenheit: Double,
    timestamp: OffsetDateTime,
    `type`: String,
    precision: Temperature,
  ) derives Decoder

  /**
   * Temperature in Celsius and Fahrenheit
   */
  final case class Temperature(
    celsius: Double,
    fahrenheit: Double,
  ) derives Decoder

  /**
   * Solar intensity as a percentage with timestamp
   */
  final case class SolarIntensity(
    `type`: String,
    percentage: Double,
    timestamp: OffsetDateTime,
  ) derives Decoder

  /**
   * Weather condition state with value and timestamp
   */
  final case class WeatherState(
    `type`: String,
    value: String,
    timestamp: OffsetDateTime,
  ) derives Decoder

}
