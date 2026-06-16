package com.colofabrix.scala.tado4s.api

import io.circe.Decoder

/**
 * OAuth2 token response containing access and refresh tokens
 */
final case class AuthResponse(
  access_token: String,
  token_type: String,
  refresh_token: String,
  expires_in: Int,
  scope: String,
  jti: Option[String] = None,
) derives Decoder
