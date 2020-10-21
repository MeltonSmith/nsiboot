NsiBoot demo
--------------------------------------

Nsi based on working with Apache Kafka


Текущие TODO:
--------------------------------------
    - make a descrption for Avro converting


PostgreSQL
--------------------------------------
    - When working with Debezium the following parameters are needed to be set:
        wal_level = logical;
        max_wal_senders = 1;
        max_replication_slots = 1;
     Options with Docker:
         # get the default config. Loads into /home on Linux
        docker run -i --rm postgres cat /usr/share/postgresql/postgresql.conf.sample > my-postgres.conf
        # Customize this default config by setting wal_level, max_wal_senders and max_replication_slots (but don't forget to uncomment these rows) + run with customized config file, to make all this work.
        #Example:
        docker run -p 2746:5432 -d --name some-postgres -v "/home/yarahmatullin/my-postgres.conf":/etc/postgresql/postgresql.conf -e POSTGRES_PASSWORD=postgres postgres -c 'config_file=/etc/postgresql/postgresql.conf'

        #or use a volume for pg container (like I did in docker-compose) and change the options there manually
ZooKeeper
--------------------------------------
    -  docker run -it --rm --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 debezium/zookeeper:1.3
    
Kafka
--------------------------------------
    -  docker run -it --rm --name kafka -p 9092:9092 
        -e KAFKA_MESSAGE_MAX_BYTES=10485760
        -e KAFKA_OFFSET_STORAGE_PARTITION=25 
        -e KAFKA_OFFSET_STORAGE_REPLICATION_FACTOR=1
        -e KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS=3 
        --link zookeeper:zookeeper 
        debezium/kafka:1.3
        
Debezium Connect
--------------------------------------
    docker run -it --rm --name connect -p 8083:8083 
    -e GROUP_ID=1 -e CONFIG_STORAGE_TOPIC=my_connect_configs 
    -e OFFSET_STORAGE_TOPIC=my_connect_offsets -e OFFSET_FLUSH_TIMEOUT_MS=60000 
    -e OFFSET_FLUSH_INTERVAL_MS=10000 -e CONNECT_MAX_REQUEST_SIZE=10485760 
    -e CONNECT_PRODUCER_MAX_REQUEST_SIZE=10485760 
    -e STATUS_STORAGE_TOPIC=my_connect_statuses 
    --link zookeeper:zookeeper 
    --link kafka:kafka debezium/connect:1.3

Watcher container example
--------------------------------------
     Local containers:
     docker run -it --rm 
     --name watcher 
     --link zookeeper:zookeeper 
     -link kafka:kafka debezium/kafka:1.3 watch-topic -a 
     -k nsitrunk_uni.public.person_t
     
     Remote Kafka and Zoo:
     docker run -it --rm 
     --name watcher 
     -e ZOOKEEPER_CONNECT=kafka.tandemservice.ru:2181 
     -e KAFKA_BROKER=kafka.tandemservice.ru:9092 debezium/kafka:1.2 watch-topic -a 
     -k nsitrunk_uni.public.yeardistributionpart_t

Posting a connector
-------------------------------------
    REMOTE DB and Kafka:
    curl -i -X POST 
    -H "Accept:application/json" 
    -H "Content-Type:application/json" 
    localhost:8083/connectors/ 
    -d "{ \"name\": \"test-connector\", 
    \"config\": 
    { \"connector.class\": \"io.debezium.connector.postgresql.PostgresConnector\", 
    \"tasks.max\": \"1\", \"database.hostname\": \"psql2.tandemservice.ru\",
     \"database.port\": \"5432\", 
     \"database.user\": \"postgres\", 
     \"database.password\": \"postgres\", 
     \"database.dbname\": \"nsitrunk_uni\",
     \"database.server.id\": \"11112\", 
     \"database.server.name\": \"nsitrunk_uni\",
      \"database.whitelist\": \"nsitrunk_uni\", 
      \"database.history.kafka.bootstrap.servers\":\"kafka.tandemservice.ru:9092\", 
      \"database.history.kafka.topic\": \"dbhistory.nsitrunk_uni\", 
      \"tasks.max\": \"1\", 
      \"max.queue.size\": \"81290\",
      \"max.batch.size\": \"20480\", 
      \"plugin.name\": \"pgoutput\"}}"
      
      In case of local container with db use "database.hostname\": $(container name) instead
      
Checking a connector
-------------------------------------
        curl -H "Accept:application/json" localhost:8083/connectors/
        
Deleting a connector
-------------------------------------
        curl -X DELETE localhost:8083/connectors/${connector name}    
        
Monitoring
-------------------------------------     
     #monitor example. $dbname.public.$tableName (link legacy)
      docker run -it --rm --name watcher --link zookeeper:zookeeper --link kafka:kafka debezium/kafka:1.3 watch-topic -a -k testDbTrunk.public.abstractparagraphkey_t      
      
      (default networking with net created by docker compose)
      docker run -it --rm --name watcher --net resources_default -e ZOOKEEPER_CONNECT=zookeeper:2181 -e KAFKA_BROKER=kafka:9092 debezium/kafka:1.3 watch-topic -a -k testDbTrunk.public.abstractparagraphkey_t      
     
     #list of topics
       docker run -it --rm --net resources_default -e ZOOKEEPER_CONNECT=zookeeper:2181  debezium/kafka:1.3 list-topics
      
     #monitoring via a standart consumer (use exec bash inside of kafka container) in case of a separate machine with Kafka replace 0.0.0.0 with localhost
       bin/kafka-console-consumer.sh --topic testDbTrunk.public.abstractemployeeextract_t --from-beginning --bootstrap-server 0.0.0.0:9092