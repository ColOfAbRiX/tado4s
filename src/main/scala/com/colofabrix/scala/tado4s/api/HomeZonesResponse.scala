package com.colofabrix.scala.tado4s.api

import io.circe.Decoder
import java.time.*

/**
 * Zone configuration including name, type, and associated devices
 */
final case class HomeZoneResponse(
  id: Int,
  name: String,
  `type`: String,
  dateCreated: OffsetDateTime,
  deviceTypes: Vector[String],
  devices: Vector[HomeZoneResponse.Device],
  reportAvailable: Boolean,
  showScheduleSetup: Boolean,
  supportsDazzle: Boolean,
  dazzleEnabled: Boolean,
  dazzleMode: Option[HomeZoneResponse.DazzleMode],
  openWindowDetection: Option[HomeZoneResponse.OpenWindowDetection],
) derives Decoder

object HomeZoneResponse {

  /**
   * Dazzle mode settings for the zone
   */
  final case class DazzleMode(
    supported: Boolean,
    enabled: Option[Boolean],
  ) derives Decoder

  /**
   * Device assigned to a zone with firmware and connection details
   */
  final case class Device(
    deviceType: String,
    serialNo: String,
    shortSerialNo: String,
    currentFwVersion: String,
    connectionState: State[Boolean],
    characteristics: Characteristics,
    batteryState: Option[String],
    duties: Vector[String],
    mountingState: Option[State[String]],
    mountingStateWithError: Option[String],
    orientation: Option[String],
    childLockEnabled: Option[Boolean],
    isDriverConfigured: Option[Boolean],
  ) derives Decoder

  /**
   * Device characteristics including capabilities
   */
  final case class Characteristics(
    capabilities: Vector[String],
  ) derives Decoder

  /**
   * A typed value with a timestamp
   */
  final case class State[A](
    value: A,
    timestamp: OffsetDateTime,
  ) derives Decoder

  /**
   * Open window detection settings for a zone
   */
  final case class OpenWindowDetection(
    supported: Boolean,
    enabled: Option[Boolean],
    timeoutInSeconds: Option[Int],
  ) derives Decoder

}
