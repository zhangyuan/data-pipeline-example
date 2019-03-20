name := "data-pipeline"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "1.6.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka" % "1.6.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.1.0"



