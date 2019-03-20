package datapipeline

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class KafkaSink(createProducer: () => KafkaProducer[String, String]) extends Serializable {
  lazy val producer = createProducer()

  def send(topic: String, value: String): Unit = producer.send(new ProducerRecord(topic, value))
}

object KafkaSink {
  def apply(properties: Properties): KafkaSink = {
    val f = () => {
      val producer = new KafkaProducer[String, String](properties)

      sys.addShutdownHook {
        producer.close()
      }

      producer
    }
    new KafkaSink(f)
  }
}