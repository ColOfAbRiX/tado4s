package com.colofabrix.scala.tado4s.api

import io.circe.Decoder
import java.util.UUID

/**
 * User account information including homes and mobile devices
 */
final case class AccountResponse(
  name: String,
  email: String,
  username: String,
  id: UUID,
  homes: Vector[AccountResponse.Home],
  locale: String,
  mobileDevices: Vector[AccountResponse.MobileDevice],
) derives Decoder

object AccountResponse {

  /**
   * Home associated with the account
   */
  final case class Home(
    id: Int,
    name: String,
  ) derives Decoder

  /**
   * Mobile device registered to the account
   */
  final case class MobileDevice(
    name: String,
    id: Int,
    settings: Settings,
    location: Option[Location],
    deviceMetadata: DeviceMetadata,
  ) derives Decoder

  /**
   * Device metadata including platform and model information
   */
  final case class DeviceMetadata(
    platform: String,
    osVersion: String,
    model: String,
    locale: String, // Can be changed to java.util.Locale
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
   * Mobile device settings including geo-tracking and notification preferences
   */
  final case class Settings(
    geoTrackingEnabled: Boolean,
    specialOffersEnabled: Boolean,
    onDemandLogRetrievalEnabled: Boolean,
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
    energyIqReminder: Boolean,
    tariffHighPriceAlert: Boolean,
    tariffLowPriceAlert: Boolean,
  ) derives Decoder

}
