package streams;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Predicate;


public class JsonSample {
    final static String APPLICATION_ID = "json-sample-v0.1.0";
    final static String APPLICATION_NAME = "JSON Sample";

    // POJO class
    static public class TempReading {
        public String station;
        public Double temperature;
        public Long timestamp;
    }

    public static void main(String[] args) {
        System.out.printf("*** Starting %s Application ***%n", APPLICATION_NAME);

        StreamsConfig config = getConfig();
        Topology topology = getTopology();
        KafkaStreams streams =  startApp(config, topology);

        setupShutdownHook(streams);
    }

    private static Topology getTopology(){
        StreamsBuilder builder = new StreamsBuilder();

        final Serde<String> stringSerde = Serdes.String();
        final Serde<TempReading> temperatureSerde = getJsonSerde();

        // TODO: here we construct the Kafka Streams topology
        builder.stream("temperatures-topic", Consumed.with(stringSerde, temperatureSerde))
            .filter((key,value) -> value.temperature > 25)
            .to("high-temperatures-topic", Produced.with(stringSerde, temperatureSerde));

        Topology topology = builder.build();
        return topology;
    }

    private static Serde<TempReading> getJsonSerde(){
        // TODO: create the JSON serde
        Map<String, Object> serdeProps = new HashMap<>();
        final Serializer<TempReading> temperatureSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", TempReading.class);
        temperatureSerializer.configure(serdeProps, false);
        final Deserializer<TempReading> temperatureDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", TempReading.class);
        temperatureDeserializer.configure(serdeProps, false);
        return Serdes.serdeFrom(temperatureSerializer, temperatureDeserializer);
    }

    private static StreamsConfig getConfig(){
        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        StreamsConfig config = new StreamsConfig(settings);
        return config;        
    }

    private static KafkaStreams startApp(StreamsConfig config, Topology topology){
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
        return streams;
    }

    private static void setupShutdownHook(KafkaStreams streams){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.printf("### Stopping %s Application ###%n", APPLICATION_NAME);
            streams.close();
        }));
    }
}