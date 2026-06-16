package com.colofabrix.scala.tado4s.api

import io.circe.{ Decoder, Encoder }

/**
 * Response for active timetable
 */
final case class ActiveTimetableResponse(
  id: Int,
  `type`: String,
) derives Decoder, Encoder.AsObject

/**
 * Response for listing all timetables
 */
final case class TimetablesResponse(
  timetables: Vector[TimetablesResponse.Timetable],
) derives Decoder

object TimetablesResponse {

  /**
   * Schedule timetable with ID and type
   */
  final case class Timetable(
    id: Int,
    `type`: String,
  ) derives Decoder

}

/**
 * Response for timetable blocks
 */
final case class TimetableBlocksResponse(
  blocks: Vector[TimetableBlocksResponse.Block],
) derives Decoder

object TimetableBlocksResponse {

  /**
   * Schedule block for a specific day type with time range and setting
   */
  final case class Block(
    dayType: String,
    start: String,
    end: String,
    geolocationOverride: Boolean,
    setting: Setting,
  ) derives Decoder, Encoder.AsObject

  /**
   * Heating setting with type, power state, and optional temperature
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
