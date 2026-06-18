import org.typelevel.scalacoptions.ScalacOptions
import xerial.sbt.Sonatype._

// Project Information

val scala3Version = "3.3.7"

val caseInsensitiveVersion = "1.5.0"
val catsEffectVersion      = "3.5.7"
val catsVersion            = "2.12.0"
val circeCoreVersion       = "0.14.15"
val enumeratumVersion      = "1.9.8"
val fs2Version             = "3.12.2"
val h4sblVersion           = "1.1.0"
val http4sClientVersion    = "0.23.34"
val log4catsVersion        = "2.8.0"
val pureconfigVersion      = "0.17.10"
val scalatestVersion       = "3.2.20"
val scodecBitsVersion      = "1.2.5"

// Global Settings

Global / run / fork              := true
Global / onChangedBuildSource    := ReloadOnSourceChanges
Global / tpolecatExcludeOptions ++= Set(ScalacOptions.warnUnusedLocals, ScalacOptions.warnUnusedImports)
Test / tpolecatScalacOptions     := Set.empty
isSnapshot := true
addCommandAlias(
  "styleApply",
  "; set ThisBuild / scalacOptions += \"-Wunused:all\"; scalafixEnable; scalafixAll; session clear; scalafmtAll"
)
addCommandAlias(
  "styleCheck",
  "; set ThisBuild / scalacOptions += \"-Wunused:all\"; scalafixEnable; scalafixAll --check; session clear; scalafmtCheckAll"
)

lazy val root =
  project
    .in(file("."))
    .settings(
      name                 := "tado4s",
      version              := "2.0.0",
      organization         := "com.colofabrix.scala",
      scalaVersion         := scala3Version,
      libraryDependencies ++= List(
        "co.fs2"                %% "fs2-core"            % fs2Version,
        "co.fs2"                %% "fs2-io"              % fs2Version,
        "com.beachape"          %% "enumeratum-circe"    % enumeratumVersion,
        "com.beachape"          %% "enumeratum"          % enumeratumVersion,
        "com.colofabrix.scala"  %% "h4sbl"               % h4sblVersion,
        "com.github.pureconfig" %% "pureconfig-core"     % pureconfigVersion,
        "io.circe"              %% "circe-core"          % circeCoreVersion,
        "io.circe"              %% "circe-parser"        % circeCoreVersion % Test,
        "org.http4s"            %% "http4s-circe"        % http4sClientVersion,
        "org.http4s"            %% "http4s-client"       % http4sClientVersion,
        "org.http4s"            %% "http4s-core"         % http4sClientVersion,
        "org.http4s"            %% "http4s-ember-client" % http4sClientVersion,
        "org.scalatest"         %% "scalatest"           % scalatestVersion % Test,
        "org.scodec"            %% "scodec-bits"         % scodecBitsVersion,
        "org.typelevel"         %% "case-insensitive"    % caseInsensitiveVersion,
        "org.typelevel"         %% "cats-core"           % catsVersion,
        "org.typelevel"         %% "cats-effect-kernel"  % catsEffectVersion,
        "org.typelevel"         %% "cats-effect-std"     % catsEffectVersion,
        "org.typelevel"         %% "cats-effect"         % catsEffectVersion,
        "org.typelevel"         %% "cats-kernel"         % catsVersion,
        "org.typelevel"         %% "log4cats-core"       % log4catsVersion,
        "org.typelevel"         %% "log4cats-slf4j"      % log4catsVersion,
      ),
      semanticdbEnabled := true,
      semanticdbVersion := scalafixSemanticdb.revision,
    )
    .settings(publishSettings)

// Publishing Settings

lazy val publishSettings =
  Seq(
    homepage             := Some(url("https://github.com/ColOfAbRiX/tado4s")),
    startYear            := Some(2024),
    organizationName     := "ColOfAbRiX",
    organizationHomepage := Some(url("https://github.com/ColOfAbRiX")),
    licenses             := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    scmInfo              := Some(
      ScmInfo(
        url("https://github.com/ColOfAbRiX/tado4s"),
        "scm:git@github.com:ColOfAbRiX/tado4s.git",
      ),
    ),
    developers := List(
      Developer(
        "ColOfAbRiX",
        "Fabrizio Colonna",
        "colofabrix@tin.it",
        url("https://github.com/ColOfAbRiX"),
      ),
    ),
    pomIncludeRepository   := { _ => false },
    publishMavenStyle      := true,
    sonatypeProjectHosting := Some(
      GitHubHosting("ColOfAbRiX", "tado4s", "colofabrix@tin.it"),
    ),
    publishTo := {
      if (isSnapshot.value)
        Some(Resolver.sonatypeCentralSnapshots)
      else
        localStaging.value
    },

    // Scaladoc settings
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title",
      "Tado4s API Documentation",
      "-doc-version",
      version.value,
      "-encoding",
      "UTF-8",
    ),
  )
