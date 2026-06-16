package com.colofabrix.scala.tado4s.store

import cats.effect.Sync
import cats.implicits.given
import com.typesafe.config.ConfigRenderOptions
import java.nio.file.{ Files, Path }
import pureconfig.*

/**
 * Persistent token storage with file system backing.
 */
object Tado4sTokenStore {

  /**
   * Load the token store
   */
  def load[F[_]: Sync](tokenPath: Path): F[Option[TadoRefreshToken]] =
    Sync[F]
      .blocking(ConfigSource.file(tokenPath.toFile).load[TadoRefreshToken])
      .map {
        case Right(token) =>
          Some(token)
        case Left(_) =>
          None
      }

  /**
   * Save the refresh token
   */
  def save[F[_]: Sync](tokenPath: Path)(token: TadoRefreshToken): F[Unit] =
    Sync[F].blocking {
      val configValue   = ConfigWriter[TadoRefreshToken].to(token)
      val renderOptions = ConfigRenderOptions.concise().setFormatted(true).setJson(false)
      val content       = configValue.render(renderOptions)
      Files.writeString(tokenPath, content)
      ()
    }

  /**
   * Delete the token file
   */
  def clear[F[_]: Sync](tokenPath: Path): F[Unit] =
    Sync[F].blocking {
      try Files.deleteIfExists(tokenPath)
      catch case _: java.io.IOException => ()
      ()
    }

}
