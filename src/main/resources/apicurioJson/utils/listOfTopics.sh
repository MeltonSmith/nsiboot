docker run -it --rm --net apicuriojson_default -e ZOOKEEPER_CONNECT=zookeeper:2181  debezium/kafka:1.3 list-topics
$SHELL