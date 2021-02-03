package taxi_utils

import com.example.data_api.taxi_utils.DistanceDistribution
import org.apache.spark.sql.QueryTest.checkAnswer
import org.apache.spark.sql.Row
import org.apache.spark.sql.test.SharedSparkSession

class DistanceDistributionTest extends SharedSparkSession {
  System.setProperty("hadoop.home.dir", "/")

  val dataPath = "src/main/resources/data"

  test("Distance Distribution Test") {
    val taxiInfoDF = spark.read.parquet(s"$dataPath/new_york_taxi_data")
    val taxiDictDF = spark.read
      .option("header", "true")
      .csv(s"$dataPath/taxi_zones.csv")

    val distanceDistribution = DistanceDistribution(taxiInfoDF, taxiDictDF, saveData = false)

    checkAnswer(
      distanceDistribution,
        Row("Manhattan", 295642, 2.1884251899256144, 2.6319377112494804, 0.01, 37.92)  ::
        Row("Queens", 13394, 8.98944004778256, 5.420778528649564, 0.01, 51.6) ::
        Row("Brooklyn", 12587, 6.932374672280937, 4.754780022484276, 0.01, 44.8) ::
        Row("Unknown", 6285, 3.68733333333335, 5.715976934424563, 0.01, 66.0) ::
        Row("Bronx", 1562, 9.209398207426394, 5.330517246526124, 0.02, 31.18) ::
        Row("EWR", 491, 17.559816700610995, 3.761422354588497, 0.01, 45.98) ::
        Row("Staten Island", 62, 20.114032258064512, 6.892080858225576, 0.3, 33.78) :: Nil
    )
  }
}