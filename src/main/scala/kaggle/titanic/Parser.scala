package kaggle.titanic

object Parser {
  
  def parseTrainingPassenger(raw: String): TrainingPassenger = {
    val rawSplit = raw.split('\"').toIterator
    val featureSeq: Seq[String] = (rawSplit.next().split(',').toSeq :+ rawSplit.next()) ++ rawSplit.toSeq.last.split(',').toSeq.tail
    val survived = featureSeq(1).toInt == 1
    val pass = Passenger(
      id = featureSeq(0).toInt,
      pClass = featureSeq(2).toInt,
      name = featureSeq(3),
      isMale = featureSeq(4) == "male",
      age = if (featureSeq(5) == "") -1.0 else featureSeq(5).toDouble,
      sibsAndSpouse = featureSeq(6).toInt,
      parsAndChilds = featureSeq(7).toInt,
      ticket = featureSeq(8),
      fare = featureSeq(9).toDouble,
      cabin = featureSeq(10),
      embarked = if (featureSeq.isDefinedAt(11)) featureSeq(11) else ""
    )
    TrainingPassenger(pass, survived)
  }

  def parseTestingPassenger(raw: String): Passenger = {
    val rawSplit = raw.split('\"').toIterator
    val featureSeq: Seq[String] = (rawSplit.next().split(',').toSeq :+ rawSplit.next()) ++ rawSplit.toSeq.last.split(',').toSeq.tail
    Passenger(
      id = featureSeq(0).toInt,
      pClass = featureSeq(1).toInt,
      name = featureSeq(2),
      isMale = featureSeq(3) == "male",
      age = if (featureSeq(4) == "") -1 else featureSeq(5).toDouble,
      sibsAndSpouse = featureSeq(5).toInt,
      parsAndChilds = featureSeq(6).toInt,
      ticket = featureSeq(7),
      fare = if (featureSeq(8) == "") -1 else featureSeq(8).toDouble,
      cabin = featureSeq(9),
      embarked = if (featureSeq.isDefinedAt(10)) featureSeq(10) else ""
    )
  }

}