package com.urdnot.iot.bem680

import com.urdnot.iot.bme680.DataProcessor.{log, parseRecord}
import com.urdnot.iot.bme680.{DataFormatter, DataProcessor, DataStructures}
import org.scalatest.flatspec.AsyncFlatSpec

class ParseJsonSuite extends AsyncFlatSpec with DataStructures {
  val validJsonMpl3115a2: Array[Byte] = """{"timestamp": 1600656167200, "host": "pi-weather", "bme680": {"gasOhms": 45556, "pressureInHg": 29.74620401635191, "altitudeMeters": 49.471284615740736, "tempF": 64.347125, "tempC": 17.970625, "pressurePa": 1007.3220815732873, "humidity": 84.90403635322619}}""".getBytes
  val validJsonReply: bme680 = bme680(timestamp = Some(1600656167200L), pressurePa = Some(1007.3220815732873), pressureInHg = Some(29.74620401635191), tempF = Some(64.347125), tempC = Some(17.970625), humidity = Some(84.90403635322619), gasOhms = Some(45556), altitudeMeters = Some(49.471284615740736))
  val validInfluxReply: String = """outdoorBme680,sensor=bme680,host=pi-weather tempF=64.347125,tempC=17.970625,inches=29.74620401635191,altitude=49.471284615740736,PressurePa=1007.3220815732873,humidity=84.90403635322619,gasOhms=45556i 1600656167200"""

  behavior of "DataParser"
  it should "Correctly extract an object from the JSON " in {
    parseRecord(validJsonMpl3115a2, log).map { x =>
      assert(x == Right(validJsonReply))
    }
  }
  behavior of "DataFormatter"
  it should "prepare the influxdb update body " in {
    DataFormatter.prepareInflux(validJsonReply.asInstanceOf[DataProcessor.bme680]).map { x =>
      assert(x.get == validInfluxReply)
    }
  }
}
