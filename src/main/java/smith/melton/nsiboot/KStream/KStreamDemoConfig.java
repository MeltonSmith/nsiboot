package smith.melton.nsiboot.KStream;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smith.melton.nsiboot.entity.YearDistributionPart;

import java.util.Properties;

/**
 * @author Melton Smith
 * @since 07.11.2020
 */

@Configuration
public class KStreamDemoConfig {

    @Bean
    public KStreamService kStreamService(){
       return new KStreamService();
    }

    @Bean
    public Properties kStreamServiceProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "KStreamDemo");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KStreamDemo_group_id");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "KStreamDemo_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "30000");
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "10000");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "1");
        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "10000");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
//        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, StockTransactionTimestampExtractor.class);
        return props;
    }

    @Bean
    public Serde yearDistributionPartSerde(){
        var serializer = new KafkaJsonSchemaSerializer<YearDistributionPart>();
        var deserializer = new KafkaJsonSchemaDeserializer<YearDistributionPart>();
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
