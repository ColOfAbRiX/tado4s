package com.colofabrix.scala.tado4s.api

import io.circe.{ Decoder, Encoder }

/**
 * Response for away configuration
 */
final case class AwayConfigurationResponse(
  `type`: String,
  autoAdjust: Boolean,
  comfortLevel: Int,
  setting: AwayConfigurationResponse.Setting,
) derives Decoder, Encoder.AsObject

object AwayConfigurationResponse {

  /**
   * Away mode heating setting with optional temperature
   */
  final case class Setting(
    `type`: String,
    power: String,
    temperature: Option[Temperature],
  ) derives Decoder, Encoder.AsObject

  /**
   * Temperature in Celsius and Fahrenheit
   */
  final case class Temperature(
    celsius: Double,
    fahrenheit: Double,
  ) derives Decoder, Encoder.AsObject

}
