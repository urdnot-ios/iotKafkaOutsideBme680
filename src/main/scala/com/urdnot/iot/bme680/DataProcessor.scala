package com.urdnot.iot.bme680

import com.typesafe.scalalogging.Logger
import io.circe.{Json, ParsingFailure}
import io.circe.parser.parse
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DataProcessor extends DataStructures {
  val log: Logger = Logger("bme680_Processor")
  def parseRecord(record: Array[Byte], log: Logger): Future[Either[String, bme680]] = Future {
    val recordString = record.map(_.toChar).mkString
    val genericParse: Either[ParsingFailure, Json] = parse(recordString)
    import io.circe.optics.JsonPath._
    genericParse match {
      case Right(x) => x match {
        case x: Json => try {
          Right(bme680(
            timestamp = root.timestamp.long.getOption(x),
            gasOhms = root.bme680.gasOhms.int.getOption(x),
            pressureInHg = root.bme680.pressureInHg.double.getOption(x),
            pressurePa = root.bme680.pressurePa.double.getOption(x),
            tempF = root.bme680.tempF.double.getOption(x),
            tempC = root.bme680.tempC.double.getOption(x),
            humidity = root.bme680.humidity.double.getOption(x),
            altitudeMeters = root.bme680.altitudeMeters.double.getOption(x)
          ))
        } catch {
          case e: Exception => Left("Unable to extract JSON: " + e.getMessage)
        }
        case _ => Left("I dunno what this is, but it's not a door message: " + x)
      }
      case Left(x) => Left(x.getMessage)
    }
  }
}
