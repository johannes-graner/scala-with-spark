package kaggle.utils

case class LabeledData(
  id: Int,
  label: Double,
  features: org.apache.spark.ml.linalg.Vector
)