package datapipeline

import java.util.Properties

import kafka.serializer.StringDecoder
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext, Time}

object StreamingPipeline {
  def main(args: Array[String]): Unit = {
    val ssc: StreamingContext = createStringContext()
    ssc.sparkContext.setLogLevel("ERROR")

    val kafkaSink = ssc.sparkContext.broadcast(KafkaSink(kafkaSinkConfig()))

    val windowDuration = Seconds(10)

    val stream = createKafkaInputStream(ssc, windowDuration, Seconds(5))

    stream
      .map(_._2)
      .foreachRDD((rdd, time: Time) => {
        val sqlContext: SQLContext = SQLContext.getOrCreate(rdd.sparkContext)
        val sourceDF = readDataFrame(sqlContext, rdd)

        sourceDF.printSchema()
        sourceDF.show()

        val endTime = time.milliseconds
        val startTime = endTime - windowDuration.milliseconds

        process(sqlContext, sourceDF, startTime, endTime)
          .foreach((row: Row) => {
            val message = new UserCountKafkaMessage(row)
            kafkaSink.value.send("users", message.toJSON.toString())
        })
    })

    ssc.start()
    ssc.awaitTermination()
  }

  private def process(sqlContext: SQLContext, dataFrame: DataFrame, startTime: Long, endTime: Long) = {
    import sqlContext.implicits._
    dataFrame
      .groupBy($"name")
      .count()
      .withColumn("startTime", lit(startTime))
      .withColumn("endTime", lit(endTime))
  }

  private def readDataFrame(sqlContext: SQLContext, rdd: RDD[String]) = {
    val sourceSchema = StructType(Seq(
      StructField("name", DataTypes.StringType),
      StructField("id", DataTypes.IntegerType),
      StructField("timestamp", DataTypes.createDecimalType(20, 0))
    ))

    sqlContext.read.schema(sourceSchema)
      .json(rdd)
  }

  private def createKafkaInputStream(ssc: StreamingContext, windowDuration: Duration, slideDuration: Duration) = {
    val kafkaSourceParams = Map[String, String]("metadata.broker.list" -> "kafka:9092")
    val kafkaSourceTopics = "source-users".split(",").toSet

    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc,
      kafkaSourceParams,
      kafkaSourceTopics
    ).window(windowDuration, slideDuration)
  }

  private def createStringContext() = {
    val conf = new SparkConf()
      .setAppName("data-pipeline")
      .setMaster("local")
      .set("spark.driver.host", "127.0.0.1")

    val ssc = new StreamingContext(conf, Seconds(5))
    ssc
  }

  private def kafkaSinkConfig() = {
    val properties = new Properties()
    properties.put("bootstrap.servers", "kafka:9092")
    properties.put("key.serializer", classOf[StringSerializer])
    properties.put("value.serializer", classOf[StringSerializer])
    properties
  }
}
