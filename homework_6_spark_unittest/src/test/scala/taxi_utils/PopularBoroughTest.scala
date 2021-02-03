package taxi_utils

import com.example.data_api.taxi_utils.PopularBorough
import org.apache.spark.sql.QueryTest.checkAnswer
import org.apache.spark.sql.Row
import org.apache.spark.sql.test.SharedSparkSession

class PopularBoroughTest extends SharedSparkSession {
  System.setProperty("hadoop.home.dir", "/")

  val dataPath = "src/main/resources/data"

  test("Popular Borough Test") {
    val taxiInfoDF = spark.read.parquet(s"$dataPath/new_york_taxi_data")
    val taxiDictDF = spark.read
      .option("header", "true")
      .csv(s"$dataPath/taxi_zones.csv")

    val popularBorough = PopularBorough(taxiInfoDF, taxiDictDF, dataPath, saveFile = false)

    checkAnswer(
      popularBorough,
        Row("Manhattan", 296527)  ::
        Row("Queens", 13819) ::
        Row("Brooklyn", 12672) ::
        Row("Unknown", 6714) ::
        Row("Bronx", 1589) ::
        Row("EWR", 508) ::
        Row("Staten Island", 64) :: Nil
    )
  }
}
