package com.colofabrix.scala.tado4s.api

import io.circe.{ Decoder, Encoder }

/**
 * Response for mobile devices
 */
final case class MobileDevicesResponse(
  devices: Vector[MobileDevicesResponse.MobileDevice],
) derives Decoder

object MobileDevicesResponse {

  /**
   * Mobile device with settings and location
   */
  final case class MobileDevice(
    id: Int,
    name: String,
    settings: Settings,
    location: Option[Location],
    deviceMetadata: DeviceMetadata,
  ) derives Decoder

  /**
   * Mobile device settings including geo-tracking
   */
  final case class Settings(
    geoTrackingEnabled: Boolean,
    pushNotifications: Option[PushNotifications],
  ) derives Decoder

  /**
   * Push notification preferences for a mobile device
   */
  final case class PushNotifications(
    lowBatteryReminder: Boolean,
    awayModeReminder: Boolean,
    homeModeReminder: Boolean,
    openWindowReminder: Boolean,
    energySavingsReportReminder: Boolean,
    incidentDetection: Boolean,
  ) derives Decoder

  /**
   * Location of a mobile device relative to home
   */
  final case class Location(
    stale: Boolean,
    atHome: Boolean,
    bearingFromHome: BearingFromHome,
    relativeDistanceFromHomeFence: Double,
  ) derives Decoder

  /**
   * Bearing angle from home in degrees and radians
   */
  final case class BearingFromHome(
    degrees: Double,
    radians: Double,
  ) derives Decoder

  /**
   * Device metadata including platform and model information
   */
  final case class DeviceMetadata(
    platform: String,
    osVersion: String,
    model: String,
    locale: String,
  ) derives Decoder

}

/**
 * Response for mobile device settings
 */
final case class MobileDeviceSettingsResponse(
  geoTrackingEnabled: Boolean,
  pushNotifications: Option[MobileDeviceSettingsResponse.PushNotifications],
) derives Decoder, Encoder.AsObject

object MobileDeviceSettingsResponse {

  /**
   * Push notification preferences for mobile device settings
   */
  final case class PushNotifications(
    lowBatteryReminder: Boolean,
    awayModeReminder: Boolean,
    homeModeReminder: Boolean,
    openWindowReminder: Boolean,
    energySavingsReportReminder: Boolean,
    incidentDetection: Boolean,
  ) derives Decoder, Encoder.AsObject

}
