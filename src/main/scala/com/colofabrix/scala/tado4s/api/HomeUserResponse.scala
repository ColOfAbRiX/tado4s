package com.colofabrix.scala.tado4s.api

import io.circe.*

/**
 * Configured user in a home
 */
final case class HomeUserResponse(
  name: String,
  email: String,
  username: String,
  id: String,
  homes: Vector[Homes],
  locale: String,
  mobileDevices: Vector[MobileDevices],
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

/**
 * Home associated with a user
 */
final case class Homes(
  id: Int,
  name: String,
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
 * Mobile device belonging to a home user
 */
final case class MobileDevices(
  name: String,
  id: Int,
  settings: Settings,
  location: Option[Location],
  deviceMetadata: DeviceMetadata,
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

/**
 * Mobile device settings including geo-tracking and notification preferences
 */
final case class Settings(
  geoTrackingEnabled: Boolean,
  specialOffersEnabled: Boolean,
  onDemandLogRetrievalEnabled: Boolean,
  pushNotifications: PushNotifications,
) derives Decoder
