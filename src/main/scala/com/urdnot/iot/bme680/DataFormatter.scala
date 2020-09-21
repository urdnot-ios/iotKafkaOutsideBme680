package com.urdnot.iot.bme680

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DataFormatter extends DataStructures {
  def prepareInflux(structuredData: DataProcessor.bme680): Future[Option[String]] = Future {
    structuredData.toInfluxString("pi-weather")
  }
}
