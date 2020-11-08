package smith.melton.nsiboot.KStream;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Service;

/**
 * @author Melton Smith
 * @since 07.11.2020
 */
@Service
public class KStreamService {

    final Serde<String> yearDistributionPartSerde;
    final StreamsConfig streamConfig;

    public KStreamService(Serde<String> yearDistributionPartSerde, StreamsConfig streamConfig) {
        this.yearDistributionPartSerde = yearDistributionPartSerde;
        this.streamConfig = streamConfig;
    }

    public void start(){

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        streamsBuilder
                .stream("localpostgres.public.yeardistributionpart_t", Consumed.with(yearDistributionPartSerde, yearDistributionPartSerde))
                .to("streamTopicYearDistributionPart_t", Produced.with(yearDistributionPartSerde, yearDistributionPartSerde));

        //TODO deprecated constructor
        KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), streamConfig);
        streams.start();

    }

}
