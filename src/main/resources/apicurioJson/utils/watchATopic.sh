docker run -it --rm --name watcher --net apicuriojson_default -e ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_BROKER=kafka:9092 debezium/kafka:1.3 watch-topic -a -k \
storage-topic
$SHELL