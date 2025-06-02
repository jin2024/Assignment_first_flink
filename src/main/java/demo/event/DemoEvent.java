package demo.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Simple (reference,count) POJO used by all operators. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoEvent {
    private String  reference;
    private Integer count;
}
