package com.colofabrix.scala.tado4s.api

import io.circe.Decoder

/**
 * Response for heating circuits
 */
final case class HeatingCircuitsResponse(
  circuits: Vector[HeatingCircuitsResponse.HeatingCircuit],
) derives Decoder

object HeatingCircuitsResponse {

  /**
   * Individual heating circuit with driver serial numbers
   */
  final case class HeatingCircuit(
    number: Int,
    driverSerialNo: String,
    driverShortSerialNo: String,
  ) derives Decoder

}
