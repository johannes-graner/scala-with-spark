package kaggle.titanic

case class Passenger(
  id: Int,
  survived: Boolean,
  pClass: Int,
  name: String,
  isMale: Boolean,
  age: Int,
  sibsAndSpouse: Int,
  parsAndChilds: Int,
  ticket: String,
  fare: Double,
  cabin: String,
  embarked: String
)