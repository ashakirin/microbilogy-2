package org.microbiology.genom2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Genom2Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Use following call format: java -jar genom-converter-2.jar <genom-coe-file-path> <genom-description-file-path> <result-file-path>");
        }

        List<GenomDto> genomDtos = new GenomFileParser().parseGenomCodeFile(args[0]);
        Map<String, DescriptionDto> descriptions = new DescriptionFileParser().parseDescriptionFile(args[1]);

        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]))) {
                String header = "Genom Code, Description\n";
                writer.write(header);
                for (GenomDto genomDto : genomDtos) {
                    String dbXref = genomDto.getProps().get("db_xref");
                    if (!descriptions.containsKey(dbXref)) {
                        System.out.println("Not found: " + dbXref);
                        continue;
                    }
                    String raw = buildRaw(genomDto, descriptions.get(dbXref));
                    writer.write(raw);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Transformation was successful, result is saved in file: " + args[2]);
    }

    private static String buildRaw(GenomDto genomDto, DescriptionDto descriptionDto) {
        String raw = genomDto.getCode() + ","
                + genomDto.getNcNotation()
                + " gene=" + descriptionDto.getGene()
                + " locus_tag=" + descriptionDto.getLocusTag()
                + " location=" + genomDto.getProps().get("location")
                + " " + descriptionDto.getProduct()
                + " " + descriptionDto.getNote() + "\n";
        return raw;
    }
}
