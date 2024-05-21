package org.microbiology.genom2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MutationCheck {
    public static final List<String> DEFAULT_MUTATIONS = List.of("TAG", "TAA", "TGA");

    public Map<String, PositionsCounters> calculateMutations(String gen) {
        return calculateMutations(gen, DEFAULT_MUTATIONS);
    }

    public Map<String, PositionsCounters> calculateMutations(String gen, List<String> mutations) {
        return mutations.stream()
                .collect(Collectors.toMap(m -> m, m -> getPositionsCounters(gen, m)));
    }

    private static PositionsCounters getPositionsCounters(String gen, String mutation) {
        PositionsCounters posCounters = new PositionsCounters();
        int index = 0;
        while(index != -1) {
            index = gen.indexOf(mutation, index);
            if (index != -1) {
                posCounters.counters[index % 3]++;
                index++;
            }
        }
        return posCounters;
    }
}
