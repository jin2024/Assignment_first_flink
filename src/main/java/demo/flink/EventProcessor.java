package demo.flink;

import demo.event.DemoEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class EventProcessor {

    // Default values – override with --txt and --bootstrap if you like.
    private static final String DEFAULT_TXT       = "wc.txt";
    private static final String DEFAULT_BOOTSTRAP = "localhost:9092";
    private static final String OUT_TOPIC         = "wc_out";

    public static void main(String[] args) throws Exception {

        /* --- parse CLI --------------------------------------------------- */
        ParameterTool params = ParameterTool.fromArgs(args);
        String txtPath   = params.get("txt",       DEFAULT_TXT);
        String bootstrap = params.get("bootstrap", DEFAULT_BOOTSTRAP);

        /* --- build execution environment --------------------------------- */
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        /* --- source: plain text file ------------------------------------- */
        DataStream<String> lines = env.readTextFile(txtPath);

        /* --- transform: tokenise and aggregate --------------------------- */
        DataStream<DemoEvent> counts = lines
                .flatMap(new LineTokenizerFunction())               // (ref,1)
                .keyBy(DemoEvent::getReference)                     // group by ref
                .reduce(new SumCounts());                           // sum → (ref,total)

        /* --- sink: write aggregated counts as JSON into Kafka ----------- */
        KafkaConnectorFactory factory = new KafkaConnectorFactory(bootstrap);
        counts.sinkTo(factory.sink(OUT_TOPIC));

        env.execute("Word-Count to Kafka");
    }

    /** Adds the counts of two DemoEvents with the same reference. */
    private static final class SumCounts implements ReduceFunction<DemoEvent> {
        @Override
        public DemoEvent reduce(DemoEvent left, DemoEvent right) {
            return DemoEvent.builder()
                    .reference(left.getReference())
                    .count(left.getCount() + right.getCount())
                    .build();
        }
    }
}
