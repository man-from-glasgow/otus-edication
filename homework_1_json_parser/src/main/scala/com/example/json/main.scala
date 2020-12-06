package com.example.json

import java.io.PrintWriter

import com.typesafe.scalalogging.LazyLogging

import scala.io.{BufferedSource, Source}
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

object main extends LazyLogging {
  case class CountryParam(name: String, capital: String, area: BigInt)

  def prepareArgs(args: Array[String]): (String, String) = {
    if (args.length == 0) {
      logger.warn("Usage: [region_name] [file_name]")
    }

    val region_name: String = args(0)
    val file_name: String = args(1)

    (region_name, file_name)
  }

  def source: BufferedSource = Source.fromURL(
    "https://raw.githubusercontent.com/mledoze/countries/master/countries.json"
  )

  def prepareCountryList(region_name: String)(data: JValue): List[CountryParam] = {
    for {
      JObject(child) <- data
      JField("name", JObject(name)) <- child
      JField("official", JString(country_name)) <- name
      JField("capital", JArray(capital)) <- child
      JField("area", JInt(area)) <- child
      JField("region", JString(region)) <- child

      if region == region_name
    } yield CountryParam(country_name, capital.head.toString, area)
  }


  def getTopCountry(country_list: List[CountryParam]): List[CountryParam] = {
    country_list.sortBy(_.area).reverse.take(10)
  }

  def writeJsonFile(data: List[CountryParam], file_name: String): Unit = {
    val json = data.map {
      country =>
        ("name" -> country.name) ~ ("capital" -> country.capital) ~ ("area" -> country.area)
    }

    val file_path = s"/tmp/$file_name"

    val writer = new PrintWriter(file_path)
    writer.println(compact(render(json)))
    writer.flush()
    writer.close()

    logger.info(s"Create file: $file_path")
  }

  def main(args: Array[String]): Unit = {
    val (region_name, file_name) = prepareArgs(args)

    logger.info(s"Start collect data for region: $region_name")

    val json_data = parse(source.getLines().mkString)
    val country_list = prepareCountryList(region_name)(json_data)

    logger.info(s"Found ${country_list.size} countries for region: $region_name")

    val top_country = getTopCountry(country_list)

    writeJsonFile(top_country, file_name)

    logger.info("End")
  }
}
