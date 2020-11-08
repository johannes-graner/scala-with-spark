package kaggle.titanic

import kaggle.utils._
import scala.io.Source
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.log4j.{Level, Logger}

case class Passenger(
  id: Int,
  pClass: Int,
  name: String,
  isMale: Boolean,
  age: Double,
  sibsAndSpouse: Int,
  parsAndChilds: Int,
  ticket: String,
  fare: Double,
  cabin: String,
  embarked: String
)

case class TrainingPassenger(
  p: Passenger,
  survived: Boolean
)

object Titanic extends App {

  private val embarkMap = Map("" -> 0.0, "C" -> 1.0, "S" -> 2.0, "Q" -> 3.0)

  def getFeatures(p: Passenger): Vector = {
    Vectors.dense(
      p.pClass,
      if (p.isMale) 1.0 else 0.0,
      p.age,
      p.sibsAndSpouse,
      p.parsAndChilds
      //p.fare
      //embarkMap(p.embarked)
    )
  }

  override def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val spark = SparkSession
        .builder()
        .appName("titanic")
        .master("local")
        .config("spark.default.parallelism", "1")
        .getOrCreate()

    import spark.implicits._

    try {

      val rootPath = "src/test/resources/kaggle/titanic/"
      val trainData = rootPath + "train.csv"
      val testData = rootPath + "test.csv"

      val trainPassengers = Source.fromFile(trainData).getLines.toSeq.tail
      val testPassengers = Source.fromFile(testData).getLines.toSeq.tail

      val rawTrainDSWithSurvival = trainPassengers.map(Parser.parseTrainingPassenger).toDS
      val survivalDS = rawTrainDSWithSurvival.select($"p.id", $"survived")
      val rawTrainDS = rawTrainDSWithSurvival.map(_.p)

      val rawTestDS = testPassengers.map(Parser.parseTestingPassenger).toDS

      val allDS = rawTrainDS.union(rawTestDS)

      val ageDS = allDS.filter($"age" >= 0)
      val avgAge = ageDS.agg(Map("age" -> "avg")).head.getDouble(0)

      val fareByClassDS = allDS.filter($"fare" >= 0).groupBy("pClass")
      val avgFareDS = fareByClassDS
        .avg("fare")
        .toDF("pClass", "fare")
        .as[(Int,Double)]

      def cleanPass(p: Passenger): Passenger = {
        if (p.age == -1)
          return cleanPass(p.copy(age = avgAge))
        else
          p
      }

      val cleanTrainDS = rawTrainDS.map(cleanPass)      
      val cleanTestDS = rawTestDS.map(cleanPass)

      // TODO: Join non-existing fares with avgFareDS using id

      // TODO: Make into (Passenger, survived) before making labeledData
      val featureTrainDS = cleanTrainDS.join(survivalDS, "id").map(tp => LabeledData(tp.p.id, if (tp.survived) 1.0 else 0.0, getFeatures(tp.p)))
      val featureTestDF = cleanTestDS.map(p => (p.id, getFeatures(p))).toDF("id", "features")

      val lr = new LogisticRegression()
      //println(s"LogisticRegression parameters:\n ${lr.explainParams()}\n")

      lr
        .setMaxIter(100)
        .setRegParam(0.1)
        .setTol(1e-10)

      val model = lr.fit(featureTrainDS)
      println(s"Model was fit using parameters: ${model.parent.extractParamMap}")

      val predDF = model.transform(featureTestDF)

      predDF.show(false)
      //println(s"Accuracy: ${predDF.filter($"label" === $"prediction").count.toDouble/predDF.count}")
    } finally {
      spark.stop
    }
  }

}