package datapipeline

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object WorldCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("data-pipeline")
      .setMaster("local")
      .set("spark.driver.host","127.0.0.1")
    val sc = new SparkContext(conf)

    val data = sc.parallelize(Seq("this is ok", "how about you"))

    val count = data.flatMap(x => x.split("\\s")).count()

    println(count)
  }
}
