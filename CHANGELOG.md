# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-04-14

- Fixed missing weather type

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

[1.0.0]: https://github.com/ColOfAbRiX/tado4s/releases/tag/v1.0.0
