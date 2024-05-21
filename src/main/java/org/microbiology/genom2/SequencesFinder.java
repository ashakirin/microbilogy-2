package org.microbiology.genom2;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SequencesFinder {
    private final List<String> sequences;

    public SequencesFinder(String sequencesFile) throws IOException {
        if (sequencesFile != null) {
            String sequencesContent = readFile(sequencesFile);
            String[] sequencesStrings = sequencesContent.split(",");
            sequences = Arrays.stream(sequencesStrings)
                    .map(String::trim)
                    .collect(Collectors.toList());
        } else {
            sequences = List.of();
        }
    }

    public String buildHeaders() {
        if (sequences.isEmpty()) {
            return "";
        }
        return sequences.stream()
                .collect(Collectors.joining(", ", ", ", ""));
    }

    public String buildFrequencies(String genom) {
        if (sequences.isEmpty()) {
            return "";
        }
        return sequences.stream()
                .map(s -> StringUtils.countMatches(genom, s))
                .map(String::valueOf)
                .collect(Collectors.joining(",", ",", ""));
    }

    private String readFile(String path) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(path));
        return new String(content);
    }
}
