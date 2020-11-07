package kaggle.titanic

import kaggle.utils._
import scala.io.Source

class TitanicTest extends SparkSpec {
  
  sparkTest("parsing") { spark =>

    import spark.implicits._
    
    val rootPath = "src/test/resources/kaggle/titanic/"
    val trainData = rootPath + "train.csv"
    val testPassengers = Source.fromFile(trainData).getLines.toSeq.tail

    val passDS = testPassengers.map(Parser.parsePassenger).toDS

    passDS.show

    val ageDS = passDS.filter(_.age >= 0)
    val avgAge = ageDS.agg(Map("age" -> "avg")).head.getDouble(0)

    def cleanPass(p: Passenger): Passenger = {
      if (p.age == -1)
        return p.copy(age = (avgAge + 0.5).toInt)
      p
    }

    val cleanDS = passDS.map(cleanPass)
    cleanDS.show


  }

}