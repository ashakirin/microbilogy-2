package org.microbiology.genom2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class GenomFileParser {

    public List<GenomDto> parseGenomCodeFile(String filePath) {
        List<GenomDto> result = new ArrayList<>();
        try {
            String content = Files.readString(Path.of(filePath));
            List<String> genoms = (List<String>) Arrays.asList(content.split(">lcl\\|"));
            for (String genom : genoms) {
                int headerIndex = genom.indexOf("\n");
                if (headerIndex == -1) {
                    continue;
                }
                String genomHeader = genom.substring(0, headerIndex);
                String genomCode = genom.substring(headerIndex + 1, genom.length() - 1).replaceAll("\\r|\\n", "");

                int startIdIndex = genomHeader.indexOf(".1_");
                int stopIdIndex = genomHeader.indexOf(" ");
                if (startIdIndex == -1 || stopIdIndex == -1) {
                    continue;
                }
                String ncNotation = genomHeader.substring(startIdIndex + 3, stopIdIndex);
                GenomDto genomDto = new GenomDto(genomCode, ncNotation);
                String variablesList = genomHeader.substring(stopIdIndex, genomHeader.length());
                List<String> vars = Arrays.asList(variablesList.split(" "));
                vars.forEach( v -> {
                    String trimmedVariable = v.trim().replace("[", "").replace("]", "").trim();
                    String[] variable = trimmedVariable.split("=");
                    if (variable.length == 2) {
                        genomDto.addProperty(variable[0].trim(), variable[1].trim());
                    }
                });
                result.add(genomDto);
            }
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read input genom file: " + e.getMessage(), e);
        }

    }

}
