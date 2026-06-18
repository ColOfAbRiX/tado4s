package com.colofabrix.scala.tado4s

import cats.effect.unsafe.implicits.global
import cats.effect.{ IO, Ref, Resource }
import cats.implicits.*
import com.colofabrix.scala.tado4s.store.TadoRefreshToken
import java.nio.file.{ Files, Path }
import java.time.OffsetDateTime
import org.http4s.*
import org.http4s.client.Client
import org.scalatest.Assertion
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.ci.CIString
import scala.concurrent.Future
import scala.concurrent.duration.*

class Tado4sAuthenticationSpec extends AsyncFreeSpec with Matchers {

  private def runIO(io: IO[Assertion]): Future[Assertion] = io.unsafeToFuture()

  private def tempTokenPath(): IO[Path] =
    IO.blocking {
      val dir = Files.createTempDirectory("tado4s-test")
      dir.resolve("token.conf")
    }

  private def testConfig(tokenPath: Path): Tado4sConfig =
    Tado4sConfig(
      apiBase = Uri.unsafeFromString("https://api.example.com"),
      apiAuth = Uri.unsafeFromString("https://auth.example.com"),
      apiClientId = "test-client-id",
      tokenPath = tokenPath,
      httpTimeout = 10.seconds,
      httpRetriesMax = 0,
      httpRetryTimeMax = 1.second,
      ignoreSsl = true,
    )

  private val testRefreshToken =
    TadoRefreshToken("test-refresh-token", OffsetDateTime.now())

  /**
   * Creates a mock HTTP client that returns auth responses and tracks the number of auth calls via `callCount`.
   * The `delay` parameter can be used to simulate network latency.
   */
  private def mockHttpClient(
    callCount: Ref[IO, Int],
    delay: IO[Unit] = IO.unit,
  ): Client[IO] =
    Client[IO] { _ =>
      Resource.eval {
        for {
          _   <- delay
          _   <- callCount.update(_ + 1)
          json =
            s"""{"access_token":"test-access-token-${System.nanoTime()}","token_type":"bearer","refresh_token":"test-new-refresh-token","expires_in":3600,"scope":"offline_access"}"""
        } yield {
          Response[IO](status = Status.Ok)
            .withEntity(json)
            .withHeaders(Header.Raw(CIString("Content-Type"), "application/json"))
        }
      }
    }

  /**
   * Creates a mock HTTP client that always fails with an error.
   */
  private def failingHttpClient(callCount: Ref[IO, Int]): Client[IO] =
    Client[IO] { _ =>
      Resource.eval {
        callCount.update(_ + 1) >>
        IO.raiseError(new RuntimeException("Simulated auth failure"))
      }
    }

  "Tado4sAuthentication concurrency" - {

    "parallel authenticate calls should result in a single API call" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = mockHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        // Launch 50 parallel authentication calls
        _     <- (0 until 50).toList.parTraverse_(_ => auth.authenticate(testRefreshToken))
        count <- callCount.get
      } yield {
        count shouldBe 1
      }
    }

    "parallel authenticate calls should all succeed" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = mockHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        // Launch 50 parallel auth calls and get authenticated clients
        results <- (0 until 50).toList.parTraverse(_ =>
          auth.authenticate(testRefreshToken) >> auth.getAuthenticatedClient()
        )
      } yield {
        results should have size 50
      }
    }

    "concurrent getAuthenticatedClient calls during refresh should result in a single refresh" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = mockHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        // First authenticate
        _      <- auth.authenticate(testRefreshToken)
        count1 <- callCount.get
        // All subsequent getAuthenticatedClient calls should use cached token
        _      <- (0 until 50).toList.parTraverse_(_ => auth.getAuthenticatedClient())
        count2 <- callCount.get
      } yield {
        count1 shouldBe 1
        count2 shouldBe 1 // No additional calls needed
      }
    }

    "authenticate should propagate errors to all waiters" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = failingHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        // Launch parallel auth calls that should all fail
        results <- (0 until 10).toList.parTraverse: _ =>
          auth.authenticate(testRefreshToken).attempt
        count <- callCount.get
      } yield {
        // Only one actual API call should have been made
        count shouldBe 1
        // All results should be Left (errors)
        results.foreach(_ shouldBe a[Left[?, ?]])
        succeed
      }
    }

    "logout followed by re-authenticate should work" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = mockHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        _         <- auth.authenticate(testRefreshToken)
        count1    <- callCount.get
        _         <- auth.logout()
        _         <- auth.authenticate(testRefreshToken)
        count2    <- callCount.get
      } yield {
        count1 shouldBe 1
        count2 shouldBe 2 // Second auth after logout
      }
    }

    "concurrent logout and authenticate should not deadlock" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = mockHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        _         <- auth.authenticate(testRefreshToken)
        // Rapidly alternate between logout and authenticate
        _ <- (0 until 20).toList.parTraverse: i =>
          if i % 2 == 0 then auth.logout()
          else auth.authenticate(testRefreshToken)
        // Should be able to authenticate after all that
        _      <- auth.authenticate(testRefreshToken)
        client <- auth.getAuthenticatedClient()
      } yield {
        client should not be null
      }
    }

    "calling authenticate when already authenticated should be a no-op" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = mockHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        _         <- auth.authenticate(testRefreshToken)
        count1    <- callCount.get
        _         <- auth.authenticate(testRefreshToken)
        _         <- auth.authenticate(testRefreshToken)
        count2    <- callCount.get
      } yield {
        count1 shouldBe 1
        count2 shouldBe 1 // No additional calls
      }
    }

    "getAuthenticatedClient before authenticate should fail" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = mockHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        result    <- auth.getAuthenticatedClient().attempt
      } yield {
        result shouldBe a[Left[?, ?]]
      }
    }

    "high concurrency stress test" in runIO {
      for {
        tokenPath <- tempTokenPath()
        config     = testConfig(tokenPath)
        callCount <- Ref.of[IO, Int](0)
        mockClient = mockHttpClient(callCount)
        auth      <- Tado4sAuthentication(mockClient, config)
        // Mix of authenticate, getAuthenticatedClient, and logout calls
        _ <- (0 until 100).toList.parTraverse: i =>
          if i < 50 then
            auth.authenticate(testRefreshToken)
          else if i < 80 then
            auth.getAuthenticatedClient().void
          else
            auth.logout()
        // Should be in a valid state after stress
        _      <- auth.authenticate(testRefreshToken)
        client <- auth.getAuthenticatedClient()
      } yield {
        client should not be null
      }
    }

  }

}
