package taxi_utils

import com.example.data_api.taxi_utils.PopularTime
import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec

import java.time.LocalTime

class PopularTimeTest extends AnyFlatSpec {
  val dataPath = "src/main/resources/data"

  System.setProperty("hadoop.home.dir", "/")

  implicit val spark: SparkSession = SparkSession.builder()
    .appName("RDD Test")
    .config("spark.master", "local[1]")
    .getOrCreate()

  it should "upload and process data" in {
    val taxiInfoDF = spark.read.parquet(s"$dataPath/new_york_taxi_data")
    val popularTime = PopularTime(taxiInfoDF, dataPath, saveFile = false)
    val result = popularTime.take(1)

    assert(result(0)._1 == LocalTime.of(9, 51, 56))
    assert(result(0)._2 == 18)
  }
}
