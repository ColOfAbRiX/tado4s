[![CI](https://github.com/ColOfAbRiX/tado4s/actions/workflows/ci.yml/badge.svg)](https://github.com/ColOfAbRiX/tado4s/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.colofabrix.scala/tado4s_3.svg)](https://central.sonatype.com/artifact/com.colofabrix.scala/tado4s_3)
[![Scala 3](https://img.shields.io/badge/Scala-3.3-blue.svg)](https://www.scala-lang.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# Tado4s - Tado API Client for Scala

**Tado4s** is a Scala 3 library that provides a functional, type-safe client for the
[Tado API](https://www.tado.com/). It offers comprehensive access to smart thermostat data,
zone controls, and home automation features.

Built on [http4s](https://http4s.org/) and [Cats Effect](https://typelevel.org/cats-effect/),
Tado4s provides a pure functional interface to the Tado REST API with OAuth2 authentication,
automatic token refresh, persistent token storage, and typed request/response models.

## Features

- **Type-Safe API** - Strongly typed request and response models for all endpoints
- **Functional Design** - Built on Cats Effect with pure functional semantics
- **Comprehensive Coverage** - Supports homes, zones, devices, weather, day reports, and more
- **Retry Logic** - Built-in exponential backoff retry policy for API calls
- **Streaming Ready** - Returns `F[_]` effects compatible with fs2 streams

## Supported Endpoints

| Category              | Endpoints                                        |
|-----------------------|--------------------------------------------------|
| **Account**           | User account information                         |
| **Homes**             | Home details, state, and presence control        |
| **Zones**             | Zone configuration, state, and capabilities      |
| **Zone Control**      | Manual temperature control, overlays, schedules  |
| **Timetables**        | Active timetable, all timetables, blocks         |
| **Away Configuration**| Away mode settings per zone                      |
| **Devices**           | Registered device information                    |
| **Mobile Devices**    | Mobile device management and geo-tracking        |
| **Installations**     | Installation details                             |
| **Users**             | Configured user information                      |
| **Weather**           | Current weather at home location                 |
| **Day Reports**       | Historical zone data for a specific day          |
| **Heating Circuits**  | Heating circuit information                      |
| **Air Comfort**       | Air comfort and freshness data                   |

## Quick Start

### Authentication Setup

Tado4s uses OAuth2 device code flow. First, obtain a refresh token using the provided Python script:

```bash
cd scripts
python tado_device_auth.py
```

This will guide you through the authentication process and provide a refresh token.
The script outputs environment variables that you can use:

```bash
HOMEDATA_TADO_TOKEN="your-refresh-token"
HOMEDATA_TADO_TOKEN_ISSUE_TIME="2024-01-01T00:00:00+00:00"
```

### Basic Usage

Create a client and fetch account information. The client is created as a `Resource` that
properly manages the HTTP connection lifecycle:

```scala
import cats.effect.*
import com.colofabrix.scala.tado4s.*
import com.colofabrix.scala.tado4s.store.TadoRefreshToken
import java.time.OffsetDateTime

object MyApp extends IOApp.Simple {

  def run: IO[Unit] =
    Tado4sClient.make[IO]().use: client =>
      for
        initialToken = TadoRefreshToken("your-refresh-token", OffsetDateTime.now())
        _            <- client.authenticate(initialToken)
        account      <- client.getAccountInfo()
        homeId        = account.homes.head.id
        _            <- IO.println(s"Home ID: $homeId")
      yield ()

}
```

### Get Zone Information

```scala
Tado4sClient.make[IO]().use: client =>
  for
    _       <- client.authenticate(initialToken)
    account <- client.getAccountInfo()
    homeId   = account.homes.head.id
    zones   <- client.getHomeZones(homeId)
  yield zones
```

### Get Zone State

```scala
Tado4sClient.make[IO]().use: client =>
  for
    _     <- client.authenticate(initialToken)
    state <- client.getZoneState(homeId = 12345, zoneId = 1)
  yield state
```

### Get Day Report (Historical Data)

```scala
import java.time.LocalDate

Tado4sClient.make[IO]().use: client =>
  for
    _      <- client.authenticate(initialToken)
    report <- client.getZoneDayReport(
      homeId = 12345,
      zoneId = 1,
      date = LocalDate.now().minusDays(1),
    )
  yield report
```

### Get Weather

```scala
Tado4sClient.make[IO]().use: client =>
  for
    _       <- client.authenticate(initialToken)
    weather <- client.getHomeWeather(homeId = 12345)
  yield weather
```

### Streaming Day Reports

Use the streaming DSL to fetch day reports across a date range:

```scala
import com.colofabrix.scala.tado4s.Tado4sStreamDSL.*
import java.time.LocalDate

Tado4sClient.make[IO]().use: client =>
  for
    _ <- client.authenticate(initialToken)
    _ <- client.streamZoneDayReports(
           homeId = 12345,
           zoneId = 1,
           from = LocalDate.now().minusDays(7),
           to = LocalDate.now(),
         )
         .evalMap: report =>
           IO.println(s"Report for ${report.interval}: ${report.measuredData}")
         .compile
         .drain
  yield ()
```

## API Reference

### Tado4sClient

The main entry point for interacting with the Tado API.

| Method                               | Description                              |
|--------------------------------------|------------------------------------------|
| `authenticate(refreshToken)`         | Authenticate with refresh token          |
| `logout()`                           | Clear authentication and delete tokens   |
| `getAccountInfo()`                   | Get user account information             |
| `getHomeDetails(homeId)`             | Get home configuration                   |
| `getHomeZones(homeId)`               | Get zones for a home                     |
| `getHomeState(homeId)`               | Get current home state                   |
| `getHomeDevices(homeId)`             | Get registered devices                   |
| `getHomeInstallations(homeId)`       | Get installation details                 |
| `getHomeUsers(homeId)`               | Get configured users                     |
| `getZoneState(homeId, zoneId)`       | Get zone current state                   |
| `getHomeWeather(homeId)`             | Get weather at home location             |
| `getZoneDayReport(homeId, zoneId, date)` | Get historical day report            |
| `getZoneCapabilities(request)`       | Get zone temperature capabilities        |
| `getEarlyStart(request)`             | Get early start settings                 |
| `setEarlyStart(request)`             | Set early start enabled/disabled         |
| `getActiveTimetable(request)`        | Get active schedule timetable            |
| `setActiveTimetable(request)`        | Set active schedule timetable            |
| `getTimetables(request)`             | Get all schedule timetables              |
| `getTimetableBlocks(request)`        | Get schedule blocks for a timetable      |
| `getAwayConfiguration(request)`      | Get away mode configuration              |
| `setAwayConfiguration(request)`      | Set away mode configuration              |
| `setZoneOverlay(request)`            | Set manual temperature control           |
| `deleteZoneOverlay(request)`         | Clear manual control, resume schedule    |
| `setHomePresence(request)`           | Set home to HOME or AWAY                 |
| `getMobileDevices(request)`          | Get mobile devices                       |
| `getMobileDeviceSettings(request)`   | Get mobile device settings               |
| `setMobileDeviceSettings(request)`   | Set mobile device geo-tracking           |
| `deleteMobileDevice(request)`        | Remove a mobile device                   |
| `getHeatingCircuits(request)`        | Get heating circuit information          |
| `getAirComfort(request)`             | Get air comfort and freshness data       |

### Response Types

| Type                            | Description                              |
|---------------------------------|------------------------------------------|
| `AccountResponse`               | User account with home list              |
| `HomeResponse`                  | Home configuration and settings          |
| `HomeZoneResponse`              | Zone configuration                       |
| `HomeStateResponse`             | Home presence state                      |
| `HomeDeviceResponse`            | Device information                       |
| `HomeInstallationResponse`      | Installation details                     |
| `HomeUserResponse`              | User information                         |
| `ZoneStateResponse`             | Zone temperature and settings            |
| `ZoneCapabilitiesResponse`      | Zone temperature capabilities            |
| `ZoneOverlayResponse`           | Manual control overlay result            |
| `ActiveTimetableResponse`       | Currently active schedule timetable      |
| `TimetablesResponse`            | List of available schedule timetables    |
| `TimetableBlocksResponse`       | Schedule blocks for a timetable          |
| `EarlyStartResponse`            | Early start settings                     |
| `AwayConfigurationResponse`     | Away mode configuration                  |
| `MobileDevicesResponse`         | Mobile devices with location data        |
| `MobileDeviceSettingsResponse`  | Mobile device geo-tracking settings      |
| `HeatingCircuitsResponse`       | Heating circuit information              |
| `AirComfortResponse`            | Air comfort and freshness data           |
| `WeatherResponse`               | Current weather data                     |
| `DayReportResponse`             | Historical zone data for a specific day  |

### Configuration

Configure the client via `application.conf`:

```hocon
tado4s {
  api-auth                   = "https://login.tado.com"
  api-base                   = "https://my.tado.com/api/v2"
  api-client-id              = ${?TADO4S_CLIENT_ID}
  token-path                 = ~/.tado4s/token.conf
  http-timeout               = 30 seconds
  http-retries-max           = 5
  http-retry-time-max        = 1 minute
  ignore-ssl                 = false
  streaming-concurrency-max  = 4
}
```

Or programmatically:

```scala
import scala.concurrent.duration.*
import java.nio.file.Paths

val config =
  TadoConfig(
    apiBase = Uri.unsafeFromString("https://my.tado.com/api/v2"),
    apiAuth = Uri.unsafeFromString("https://login.tado.com"),
    apiClientId = "your-client-id",
    tokenPath = Paths.get(System.getProperty("user.home"), ".tado4s", "token.conf"),
    httpTimeout = 30.seconds,
    httpRetriesMax = 5,
    httpRetryTimeMax = 1.minute,
    ignoreSsl = false,
  )

Tado4sClient.make[IO](Some(config)).use: client =>
  // use client here
  IO.unit
```

### Token Persistence

Tado4s automatically persists refresh tokens to disk at the configured `token-path`
(default: `~/.tado4s/token.conf`). When authenticating:

1. The client checks for an existing stored token
2. Compares the stored token's issue time with the provided token
3. Uses the newer token for authentication
4. Saves the new refresh token after successful authentication

This ensures tokens survive application restarts and prevents token conflicts.

## Error Handling

Tado4s uses typed errors for API failures:

```scala
import com.colofabrix.scala.tado4s.*

client
  .getZoneState(homeId, zoneId)
  .handleErrorWith {
    case Tado4sError(message, Some(inner: TadoErrorResponse)) =>
      IO.println(s"API error: ${inner.errors.mkString(", ")}")
    case Tado4sError(message, Some(inner)) =>
      IO.println(s"Error: $message - ${inner.getMessage}")
    case Tado4sError(message, None) =>
      IO.println(s"Client error: $message")
  }
```

## Authentication Details

Tado uses OAuth2 with device code flow:

1. Run the authentication script: `python scripts/tado_device_auth.py`
2. Follow the prompts to authorize the application
3. Store the refresh token securely
4. Create a `TadoRefreshToken` with the token and issue time
5. Pass the refresh token to `client.authenticate()`

The client automatically:
- Handles access token refresh when needed
- Persists new refresh tokens to disk
- Uses a thread-safe state machine to prevent concurrent token refreshes (single-flight pattern)
- Applies exponential backoff retry policy for transient failures

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

Tado4s is released under the MIT license. See [LICENSE](LICENSE) for details.

## Author

[Fabrizio Colonna](mailto:colofabrix@tin.it)

## See Also

- [Tado API Reference](https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/)
- [Tado OpenAPI Spec](https://kritsel.github.io/tado-openapispec-v2/swagger.html)
- [http4s](https://http4s.org/) - Typeful, functional HTTP for Scala
- [Cats Effect](https://typelevel.org/cats-effect/) - The pure asynchronous runtime for Scala
