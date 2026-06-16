package com.colofabrix.scala.tado4s.api

import io.circe.Decoder

/**
 * Current home presence state (HOME, AWAY, or GEO_TRACKING)
 */
final case class HomeStateResponse(
  presence: String,
  presenceLocked: Boolean,
) derives Decoder
