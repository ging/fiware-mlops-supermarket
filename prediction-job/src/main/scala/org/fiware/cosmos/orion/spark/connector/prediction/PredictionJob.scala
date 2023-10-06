package org.fiware.cosmos.orion.spark.connector.prediction

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.fiware.cosmos.orion.spark.connector.{ContentType, HTTPMethod, NGSILDReceiver, OrionSink, OrionSinkObject}
import org.apache.spark.ml.feature.{VectorAssembler}
import org.apache.spark.ml.regression.{RandomForestRegressionModel}
import org.apache.spark.sql.SparkSession


case class PredictionResponse(capacity: Int, occupancy: Int, year: Int, month: Int, day: Int, time: Int, dateObserved: String) {
  override def toString :String = s"""{
  "capacity": { "value": "${capacity}", "type": "Property"},
  "occupancy": { "value":"${occupancy}", "type": "Property"},
  "year": { "value":${year}, "type": "Property"},
  "month": { "value":${month}, "type": "Property"},
  "day": { "value":${day}, "type": "Property"},
  "time": { "value": ${time}, "type": "Property"},
  "dateObserved": { "value": "${dateObserved}", "type": "Property"}
  }""".trim()
}
case class PredictionRequest(year: Int, month: Int, day: Int, weekDay: Int, time: Int, capacity: Int, dateObserved: String)

object PredictionJob {

  final val HOST_CB = sys.env.getOrElse("HOST_CB", "localhost")
  final val MASTER = sys.env.getOrElse("SPARK_MASTER", "local[*]")
  final val MODEL_VERSION = sys.env.getOrElse("MODEL_VERSION", "1")
  final val URL_CB = s"http://$HOST_CB:1026/ngsi-ld/v1/entities/urn:ngsi-ld:SupermarketForecast:001/attrs"
  final val CONTENT_TYPE = ContentType.JSON
  final val METHOD = HTTPMethod.PATCH
  final val MODEL_PATH = s"/prediction-job/model/$MODEL_VERSION"


  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .appName("PredictionJob")
      .master(MASTER)
      .config("spark.cores.max", (Runtime.getRuntime.availableProcessors / 2).toString)
      .getOrCreate()
    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))

    val vectorAssembler = new VectorAssembler()
      .setInputCols(Array("year", "month", "day", "weekDay", "time"))
      .setOutputCol("features")

    // Load model
    val model = RandomForestRegressionModel.load(MODEL_PATH)

    // Create Orion Source. Receive notifications on port 9001
    val eventStream = ssc.receiverStream(new NGSILDReceiver(9001))

    // Process event stream to get updated entities
    val processedDataStream = eventStream
      .flatMap(event => event.entities)
      .map(ent => {
        val year = ent.attrs("year")("value").toString.toInt
        val month = ent.attrs("month")("value").toString.toInt
        val day = ent.attrs("day")("value").toString.toInt
        val time = ent.attrs("time")("value").toString.toInt
        val weekDay = ent.attrs("weekDay")("value").toString.toInt
        val capacity = ent.attrs("capacity")("value").toString.toInt
        val dateObserved = ent.attrs("dateObserved")("value").toString
        PredictionRequest(year, month, day, weekDay, time, capacity, dateObserved)
      })

    // Feed each entity into the prediction model
    val predictionDataStream = processedDataStream
      .transform(rdd => {
        val df = rdd.toDF
        val vectorizedFeatures  = vectorAssembler
          .setHandleInvalid("keep")
          .transform(df)
        val predictions = model
          .transform(vectorizedFeatures)
          .select("capacity", "prediction", "year", "month", "day", "time", "dateObserved")

        predictions.toJavaRDD
    })
      .map(pred=> PredictionResponse(pred.get(0).toString.toInt,
        pred.get(1).toString.toFloat.round,
        pred.get(2).toString.toInt,
        pred.get(3).toString.toInt,
        pred.get(4).toString.toInt,
        pred.get(5).toString.toInt,
        pred.get(6).toString)
    )

    // Convert the output to an OrionSinkObject and send to Context Broker
    val sinkDataStream = predictionDataStream
      .map(res => OrionSinkObject(res.toString, URL_CB, CONTENT_TYPE, METHOD))

    // Add Orion Sink
    OrionSink.addSink(sinkDataStream)
    sinkDataStream.print()
    predictionDataStream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
