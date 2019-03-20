package datapipeline

import org.apache.spark.sql.Row
import org.codehaus.jettison.json.{JSONArray, JSONObject}

class UserCountKafkaMessage(row: Row) {
  def toJSON: JSONObject = {
    new JSONObject()
      .put("schema", buildSchema())
      .put("payload", buildPayload(row))
  }

  private def buildPayload(row: Row) = {
    val name = row.getAs[String]("name")
    val count = row.getAs[Long]("count")
    val startTime = row.getAs[Long]("startTime")
    val endTime = row.getAs[Long]("endTime")

    new JSONObject()
      .put("name", name)
      .put("count", count)
      .put("startTime", startTime)
      .put("endTime", endTime)
  }

  private def buildSchema() = {
    new JSONObject()
      .put("type", "struct")
      .put("optional", false)
      .put("fields", new JSONArray()
        .put(new JSONObject()
          .put("type", "string")
          .put("optional", false)
          .put("field", "name")
        )
        .put(new JSONObject()
          .put("type", "int64")
          .put("optional", false)
          .put("field", "count")
        )
        .put(
          new JSONObject()
            .put("type", "int64")
            .put("optional", true)
            .put("field", "endTime")
            .put("name", "org.apache.kafka.connect.data.Timestamp")
        )
        .put(
          new JSONObject()
            .put("type", "int64")
            .put("optional", true)
            .put("field", "startTime")
            .put("name", "org.apache.kafka.connect.data.Timestamp")
        )
      )
  }
}
