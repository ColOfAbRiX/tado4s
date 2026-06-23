# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2026-06-17

### Added

- Streaming DSL (`Tado4sStreamDSL.streamZoneDayReports`) for fetching day reports across a date range with configurable concurrency
- `streamingConcurrencyMax` configuration option to control parallel fetches
- `TadoConfigError` typed error for configuration loading failures
- Concurrency test suite for `Tado4sAuthentication` with single-flight, stress, and deadlock scenarios
- CI, Maven Central, and Scala 3 badges in README
- Added logging on creation

### Changed

- **BREAKING:** Scala version changed to 3.3.7 LTS for broader binary compatibility
- **BREAKING:** `Tado4sClient` is now created via `Tado4sClient.make[F]()` returning a `Resource`, replacing direct instantiation
- **BREAKING:** Renamed `TadoConfig` to `Tado4sConfig`
- **BREAKING:** Renamed `Tado4sConfig` fields
- **BREAKING:** `ignoreSsl` defaults to `false`
- `Tado4sConfig.config` now returns `Either[TadoConfigError, Tado4sConfig]` instead of throwing on failure
- `Tado4sConfig.toString` now masks `apiClientId`
- Updated dependencies:
  - cats-effect 3.5.7
  - circe 0.14.15
  - fs2 3.12.2
  - http4s 0.23.34
  - enumeratum 1.9.8
  - log4cats 2.8.0
  - pureconfig 0.17.10
  - scalatest 3.2.20
  - scodec-bits 1.2.5
  - case-insensitive 1.5.0
- Updated sbt plugins:
  - sbt-scalafix 0.14.7
  - sbt-tpolecat 0.5.6
  - sbt-sonatype 3.12.2

### Fixed

- Thread-safety issue in `Tado4sTokenStore.clear` that could fail under concurrent access on Windows
- `AccountResponse.MobileDevice.location` and `HomeUserResponse.MobileDevices.location` are now `Option[Location]` to handle API responses where mobile devices have no location data
- `ZoneStateResponse.nextScheduleChange`, `link`, and `activityDataPoints` are now `Option` to handle hot water zones and zones without active schedules
- `HomeZoneResponse.dazzleMode` and `openWindowDetection` are now `Option` to handle zones that don't support these features
- Documentation updated to use resource-based client creation pattern

## [1.0.1] - 2026-04-14

### Fixed

- Added missing `Snow` weather state to day report responses

## [1.0.0] - 2026-02-07

### Added

- Type-safe Scala 3 client for the Tado API with pure functional design
- OAuth2 device code flow authentication with Python helper script
- Thread-safe authentication state machine with single-flight pattern
- Automatic access token refresh when tokens expire
- Persistent token storage to disk at configurable path
- Exponential backoff retry policy for transient failures
- Account, home, zone, device, weather, and day report APIs
- Zone control APIs for temperature overlays and schedules
- Mobile device management and geo-tracking APIs
- Heating circuits and air comfort APIs
- PureConfig-based configuration loading
- Typed error handling with `Tado4sError`
- Optional SSL validation bypass for development

[2.0.0]: https://github.com/ColOfAbRiX/tado4s/releases/tag/2.0.0
[1.0.1]: https://github.com/ColOfAbRiX/tado4s/releases/tag/1.0.1
[1.0.0]: https://github.com/ColOfAbRiX/tado4s/releases/tag/1.0.0
