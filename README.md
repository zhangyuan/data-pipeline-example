# data-pipeline-example
Data Pipeline Example with Spark Streaming

## Structure

![Structure](https://raw.githubusercontent.com/zhangyuan/data-pipeline-example/master/data-pipeline.png)

## How to run it?

Add the hosts to `/etc/hosts`:

```
127.0.0.1 kafka
127.0.0.1 spark
127.0.0.1 zookeeper
127.0.0.1 hadoop
127.0.0.1 postgres
127.0.0.1 database
```

Run zookeeper, hadoop, kafka and PostgreSQL:

```
docker-compose up
```

Run Kafka connect source connector and sink connector:

```
docker-compose -f docker-compose-kafka-connect.yml up
```

Run the `StreamingPipeline` in IntelliJ (or other IDE).
