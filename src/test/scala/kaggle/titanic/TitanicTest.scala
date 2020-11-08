package kaggle.titanic

import kaggle.utils._
import scala.io.Source
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.param.ParamMap

class TitanicTest extends SparkSpec {
  
  sparkTest("parsing") { spark =>

    import spark.implicits._
    
    val rootPath = "src/test/resources/kaggle/titanic/"
    val trainData = rootPath + "train.csv"
    val testPassengers = Source.fromFile(trainData).getLines.toSeq.tail

    val passDS = testPassengers.map(Parser.parseTrainingPassenger).toDS

    val ageDS = passDS.filter(tp => tp.p.age >= 0)
    val avgAge = ageDS.agg(Map("p.age" -> "avg")).head.getDouble(0)

    def cleanPass(p: Passenger): Passenger = {
      if (p.age == -1)
        return p.copy(age = (avgAge + 0.5).toInt)
      p
    }

    val cleanDS = passDS.map(tp => TrainingPassenger(cleanPass(tp.p),tp.survived))
    cleanDS.show(false)

    val featureDS = cleanDS.map(tp => LabeledData(tp.p.id, if (tp.survived) 1.0 else 0.0, Titanic.getFeatures(tp.p)))
    featureDS.show(false)

    val splitRatio = 0.9

    val splitDS = featureDS.randomSplit(Array(splitRatio,1 - splitRatio), 10L)
    val trainDS = splitDS.head
    val testDS = splitDS.last

    val lr = new LogisticRegression()
    //println(s"LogisticRegression parameters:\n ${lr.explainParams()}\n")

    lr
      .setMaxIter(100)
      .setRegParam(0.1)
      .setTol(1e-10)

    val model = lr.fit(trainDS)
    println(s"Model was fit using parameters: ${model.parent.extractParamMap}")

    val predDF = model.transform(testDS)

    predDF.show(false)
    println(s"Accuracy: ${predDF.filter($"label" === $"prediction").count.toDouble/predDF.count}")

  }

}