package com.colofabrix.scala.tado4s.api

import io.circe.Decoder
import java.time.*

/**
 * Zone state including current temperature, humidity, heating power, and next schedule change
 */
final case class ZoneStateResponse(
  tadoMode: String,
  geolocationOverride: Boolean,
  setting: ZoneStateResponse.Setting,
  nextScheduleChange: ZoneStateResponse.NextScheduleChange,
  link: ZoneStateResponse.Link,
  activityDataPoints: ZoneStateResponse.ActivityDataPoints,
  sensorDataPoints: ZoneStateResponse.SensorDataPoints,
) derives Decoder

object ZoneStateResponse {

  /**
   * Activity data points for the zone including heating power
   */
  final case class ActivityDataPoints(
    heatingPower: Option[HeatingPower],
  ) derives Decoder

  /**
   * Heating power level as a percentage with timestamp
   */
  final case class HeatingPower(
    `type`: String,
    percentage: Double,
    timestamp: OffsetDateTime,
  ) derives Decoder

  /**
   * Link state of the zone
   */
  final case class Link(
    state: String,
  ) derives Decoder

  /**
   * Next scheduled temperature change
   */
  final case class NextScheduleChange(
    start: String,
    setting: Setting,
  ) derives Decoder

  /**
   * Zone heating setting with type, power state, and optional temperature
   */
  final case class Setting(
    `type`: String,
    power: String,
    temperature: Option[Temperature],
  ) derives Decoder

  /**
   * Temperature reading in both Celsius and Fahrenheit
   */
  final case class Temperature(
    celsius: Double,
    fahrenheit: Double,
  ) derives Decoder

  /**
   * Sensor data points including indoor temperature and humidity
   */
  final case class SensorDataPoints(
    insideTemperature: Option[InsideTemperature],
    humidity: Option[HeatingPower],
  ) derives Decoder

  /**
   * Indoor temperature reading with precision and timestamp
   */
  final case class InsideTemperature(
    celsius: Double,
    fahrenheit: Double,
    timestamp: OffsetDateTime,
    `type`: String,
    precision: Temperature,
  ) derives Decoder

}
