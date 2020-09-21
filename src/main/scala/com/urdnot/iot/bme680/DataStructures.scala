package com.urdnot.iot.bme680

trait DataStructures {
  val msZeros: String = "000"
  val nsZeros: String = "000000000"
  final case class bme680(
                           timestamp: Option[Long],
                           pressurePa: Option[Double],
                           pressureInHg: Option[Double],
                           tempF: Option[Double],
                           tempC: Option[Double],
                           humidity: Option[Double],
                           gasOhms: Option[Int],
                           altitudeMeters: Option[Double]
                         ) {
    def toInfluxString(host: String): Option[String] = {

      val measurement = s"""outdoorBme680,"""
      val tags: String = "sensor=bme680," +
        "host=" + host
      val fields: String = List(bme680.this.tempF match {
        case Some(i) => "tempF=" + i
        case None => ""
      }, bme680.this.tempC match {
        case Some(i) => "tempC=" + i
        case None => ""
      }, bme680.this.pressureInHg match {
        case Some(i) => "pressureInHg=" + i
        case None => ""
      }, bme680.this.altitudeMeters match {
        case Some(i) => "altitude=" + i
        case None => ""
      }, bme680.this.pressurePa match {
        case Some(i) => "PressurePa=" + i
        case None => ""
      }, bme680.this.humidity match {
        case Some(i) => "humidity=" + i
        case None => ""
      }, bme680.this.gasOhms match {
        case Some(i) => "gasOhms=" + i + "i" //REMEMBER: InfluxDB needs an 'i' after an int to force it to NOT be a float?!?
        case None => ""
      }
      ).mkString(",")
      val timestamp: String = bme680.this.timestamp.get.toString
      Some(measurement + tags + " " + fields + " " + timestamp)
    }
    private def round(d: Double): Double = BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

    def celsiusToFahrenheit(d: Double): Double = round(d * 9 / 5 + 32)

    def fahrenheitToCelsius(d: Double): Double = round((d - 32) * 5 / 9)
  }

}
