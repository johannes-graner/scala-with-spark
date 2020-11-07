package kaggle.titanic

object Parser {
  
  def parsePassenger(raw: String): Passenger = {
    val rawSplit = raw.split('\"').toIterator
    val featureSeq: Seq[String] = (rawSplit.next().split(',').toSeq :+ rawSplit.next()) ++ rawSplit.toSeq.last.split(',').toSeq.tail
    Passenger(
      id = featureSeq(0).toInt,
      survived = featureSeq(1).toInt == 1,
      pClass = featureSeq(2).toInt,
      name = featureSeq(3),
      isMale = featureSeq(4) == "male",
      age = if (featureSeq(5) == "") -1 else featureSeq(5).toDouble.toInt,
      sibsAndSpouse = featureSeq(6).toInt,
      parsAndChilds = featureSeq(7).toInt,
      ticket = featureSeq(8),
      fare = featureSeq(9).toDouble,
      cabin = featureSeq(10),
      embarked = if (featureSeq.isDefinedAt(11)) featureSeq(11) else ""
    )
  }

}