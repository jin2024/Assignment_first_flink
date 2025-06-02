package demo.flink;

import demo.event.DemoEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/** Turns every line into (reference,1) records, one per whitespace-separated token. */
@Slf4j
public class LineTokenizerFunction
        implements FlatMapFunction<String, DemoEvent> {

    @Override
    public void flatMap(String line, Collector<DemoEvent> out) {
        for (String token : line.split("\\s+")) {
            if (!token.isEmpty()) {
                log.debug("Tokenised {}", token);
                out.collect(DemoEvent.builder()
                                     .reference(token)
                                     .count(1)
                                     .build());
            }
        }
    }
}
