package com.colofabrix.scala.tado4s.api

import com.colofabrix.scala.tado4s.api.DayReportResponse.*
import enumeratum.*
import enumeratum.EnumEntry.UpperSnakecase
import io.circe.*
import java.time.*

/**
 * Historical zone data for a specific day including temperature, humidity, and heating activity
 */
final case class DayReportResponse(
  zoneType: ZoneType,
  interval: Interval,
  hoursInDay: Int,
  measuredData: MeasuredData,
  stripes: IntervalSeries[Stripes],
  settings: IntervalSeries[HeatingSetting],
  callForHeat: IntervalSeries[CallForHeat],
  hotWaterProduction: IntervalSeries[Boolean],
  weather: Weather,
)

object DayReportResponse {

  given Decoder[DayReportResponse] = Decoder.derived

  //  Time Series Containers  //

  /**
   * Time series container with individual data points
   */
  final case class PointsSeries[A](
    timeSeriesType: String,
    valueType: String,
    min: Option[A],
    max: Option[A],
    percentageUnit: Option[String],
    dataPoints: Vector[DataPoint[A]],
  ) derives Decoder

  /**
   * Time series container with interval-based data
   */
  final case class IntervalSeries[A](
    timeSeriesType: String,
    valueType: String,
    dataIntervals: Vector[DataInterval[A]],
  ) derives Decoder

  /**
   * Time series container with slot-based data mapping
   */
  final case class SlotMap[A](
    timeSeriesType: String,
    valueType: String,
    slots: Map[String, Option[A]],
  ) derives Decoder

  /**
   * A single data point with timestamp and value
   */
  final case class DataPoint[A](
    timestamp: OffsetDateTime,
    value: A,
  ) derives Decoder

  /**
   * A data interval with start/end time and optional value
   */
  final case class DataInterval[A](
    from: OffsetDateTime,
    to: OffsetDateTime,
    value: Option[A],
  ) derives Decoder

  /**
   * Stripe representing a time period type with optional heating setting
   */
  final case class Stripes(
    stripeType: StripeType,
    setting: Option[HeatingSetting],
  ) derives Decoder

  /**
   * Temperature in Celsius and Fahrenheit
   */
  final case class Temperature(
    celsius: Double,
    fahrenheit: Double,
  ) derives Decoder

  /**
   * Weather condition with state and temperature
   */
  final case class WeatherCondition(
    state: OutsideState,
    temperature: Temperature,
  ) derives Decoder

  /**
   * Heating setting with type, power state, and optional temperature
   */
  final case class HeatingSetting(
    `type`: HeatingType,
    power: Power,
    temperature: Option[Temperature],
  ) derives Decoder

  //  Enums  //

  sealed abstract class ZoneType extends EnumEntry with UpperSnakecase

  object ZoneType extends Enum[ZoneType] with CirceEnum[ZoneType] {

    case object Heating extends ZoneType

    case object HotWater extends ZoneType

    val values = findValues

  }

  sealed abstract class StripeType extends EnumEntry with UpperSnakecase

  object StripeType extends Enum[StripeType] with CirceEnum[StripeType] {

    case object Home extends StripeType

    case object Away extends StripeType

    case object OpenWindowDetected extends StripeType

    case object Manual extends StripeType

    case object OverlayActive extends StripeType

    case object MeasuringDeviceDisconnected extends StripeType

    val values = findValues

  }

  sealed abstract class HeatingType extends EnumEntry with UpperSnakecase

  object HeatingType extends Enum[HeatingType] with CirceEnum[HeatingType] {

    case object Heating extends HeatingType

    case object HotWater extends HeatingType

    val values = findValues

  }

  sealed abstract class Power extends EnumEntry with UpperSnakecase

  object Power extends Enum[Power] with CirceEnum[Power] {

    case object On extends Power

    case object Off extends Power

    val values = findValues

  }

  sealed abstract class CallForHeat(val dbValue: Int) extends EnumEntry with UpperSnakecase

  object CallForHeat extends Enum[CallForHeat] with CirceEnum[CallForHeat] {

    case object None extends CallForHeat(0)

    case object Low extends CallForHeat(1)

    case object Medium extends CallForHeat(2)

    case object High extends CallForHeat(3)

    val values = findValues

  }

  sealed abstract class OutsideState(val dbValue: Int) extends EnumEntry with UpperSnakecase

  object OutsideState extends Enum[OutsideState] with CirceEnum[OutsideState] {

    case object Cloudy extends OutsideState(1)

    case object CloudyMostly extends OutsideState(2)

    case object CloudyPartly extends OutsideState(3)

    case object Drizzle extends OutsideState(4)

    case object NightClear extends OutsideState(5)

    case object NightCloudy extends OutsideState(6)

    case object Rain extends OutsideState(7)

    case object ScatteredRain extends OutsideState(8)

    case object Sun extends OutsideState(9)

    case object Foggy extends OutsideState(10)

    case object Thunderstorm extends OutsideState(11)

    case object ScatteredSnow extends OutsideState(12)

    case object Snow extends OutsideState(13)

    val values = findValues

  }

  //  Composite Types  //

  /**
   * Time interval with start and end
   */
  final case class Interval(
    from: OffsetDateTime,
    to: OffsetDateTime,
  ) derives Decoder

  /**
   * Measured sensor data including temperature and humidity
   */
  final case class MeasuredData(
    measuringDeviceConnected: IntervalSeries[Boolean],
    insideTemperature: PointsSeries[Temperature],
    humidity: PointsSeries[Double],
  ) derives Decoder

  /**
   * Weather data for the day including conditions and sunniness
   */
  final case class Weather(
    condition: IntervalSeries[WeatherCondition],
    sunny: IntervalSeries[Boolean],
    slots: SlotMap[WeatherCondition],
  ) derives Decoder

}
