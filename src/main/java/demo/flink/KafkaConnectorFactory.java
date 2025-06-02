package demo.flink;

import demo.event.DemoEvent;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.formats.json.JsonSerializationSchema;
/** Centralises all Kafka configuration so the job code stays clean. */

public class KafkaConnectorFactory {

    private final String bootstrapServers;

    public KafkaConnectorFactory(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }


    /** Builds a Kafka sink that writes DemoEvent as JSON to the given topic . */

    public KafkaSink<DemoEvent> sink(String Topic) {
        return KafkaSink.<DemoEvent>builder()
                .setBootstrapServers(bootstrapServers)
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.<DemoEvent>builder()
                                .setTopic(Topic)
                                .setValueSerializationSchema(new JsonSerializationSchema<>())
                                .build())
                .build();
    }
}
