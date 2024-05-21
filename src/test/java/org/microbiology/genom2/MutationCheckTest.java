package org.microbiology.genom2;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MutationCheckTest {
    private MutationCheck mutationCheck = new MutationCheck();

    @Test
    public void shouldFindAllMutations() {
        String gene = "111211134441115666";

        Map<String, PositionsCounters> mutationMap = mutationCheck.calculateMutations(gene, List.of("111", "444", "666"));

        PositionsCounters c1 = mutationMap.get("111");
        PositionsCounters c2 = mutationMap.get("444");
        PositionsCounters c3 = mutationMap.get("666");

        assertThat(c1.counters[0]).isEqualTo(1);
        assertThat(c1.counters[1]).isEqualTo(1);
        assertThat(c1.counters[2]).isEqualTo(1);

        assertThat(c2.counters[0]).isEqualTo(0);
        assertThat(c2.counters[1]).isEqualTo(0);
        assertThat(c2.counters[2]).isEqualTo(1);

        assertThat(c3.counters[0]).isEqualTo(1);
        assertThat(c3.counters[1]).isEqualTo(0);
        assertThat(c3.counters[2]).isEqualTo(0);
    }
}