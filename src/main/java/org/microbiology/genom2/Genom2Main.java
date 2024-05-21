package org.microbiology.genom2;

import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Genom2Main {
    public static void main(String[] args) {
        CommandLineArgs cmdArgs = parseCommandLine(args);

        List<GenomDto> genomDtos = new GenomFileParser().parseGenomCodeFile(cmdArgs.genomFile);
        Map<String, DescriptionDto> descriptions = new DescriptionFileParser().parseDescriptionFile(cmdArgs.descriptionFile);
        MutationCheck mutationCheck = new MutationCheck();
        try {
            SequencesFinder sequencesFinder = new SequencesFinder(cmdArgs.sequencesFile);
            String header = "Genom Code, Description, TAG-intact, TAG-2-nt, TAG-1-nt, TAA-intact, TAA-2-nt, TAA-1-nt, TGA-intact, TGA-2-nt, TGA-1-n, Gen Length";
            String sequencesHeader = sequencesFinder.buildHeaders();
            header = header + sequencesHeader + "\n";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(cmdArgs.resultFile))) {
                writer.write(header);
                for (GenomDto genomDto : genomDtos) {
                    String dbXref = genomDto.getProps().get("db_xref");
                    String proteinId = genomDto.getProps().get("protein_id");
                    if (!(dbXref != null && descriptions.containsKey(dbXref))
                            && !(proteinId != null && descriptions.containsKey(proteinId))
                            || (proteinId == null && dbXref == null)) {
                        System.out.println(String.format("Not found dbXref: %s, protein_id: %s", dbXref, proteinId));
                        continue;
                    }
                    genomDto.setMutationsMap(mutationCheck.calculateMutations(genomDto.getCode()));
                    DescriptionDto descriptionDto = getDescriptionDto(descriptions, dbXref, proteinId);
                    String raw = buildRaw(genomDto, descriptionDto, sequencesFinder);
                    writer.write(raw);
                }
            }
            System.out.println("Transformation was successful, result is saved in file: " + cmdArgs.resultFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Transformation failed: " + e.getMessage());
        }
    }

    private static DescriptionDto getDescriptionDto(Map<String, DescriptionDto> descriptions, String dbXref, String proteinId) {
        if (dbXref != null && descriptions.containsKey(dbXref)) {
            return descriptions.get(dbXref);
        } else {
            return descriptions.get(proteinId);
        }
    }
    private static String buildRaw(GenomDto genomDto, DescriptionDto descriptionDto, SequencesFinder sequencesFinder) {
        String mutations = MutationCheck.DEFAULT_MUTATIONS.stream()
                .map(m -> getMutationCSV(genomDto, m))
                .collect(Collectors.joining());
        String frequencies = sequencesFinder.buildFrequencies(genomDto.getCode());
        String raw = genomDto.getCode() + ","
                + genomDto.getNcNotation()
                + " gene=" + descriptionDto.getGene()
                + " locus_tag=" + descriptionDto.getLocusTag()
                + " location=" + genomDto.getProps().get("location")
                + " " + descriptionDto.getProduct()
                + " " + descriptionDto.getNote()
                + mutations + ","
                + genomDto.getCode().length()
                + frequencies
                + "\n";
        return raw;
    }

    private static String getMutationCSV(GenomDto genomDto, String mutation) {
        PositionsCounters positionCounters = genomDto.getMutationsMap().get(mutation);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < positionCounters.counters.length; i++) {
         sb.append("," + genomDto.getMutationsMap().get(mutation).counters[i]);
        }
        return sb.toString();
    }

    private static CommandLineArgs parseCommandLine(String args[]) {
        Options options = getOptions();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            String genomFile = cmd.getOptionValue("genom");
            String descriptionFile = cmd.getOptionValue("description");
            String resultFile = cmd.getOptionValue("result");
            String sequencesFile = cmd.getOptionValue("sequences");
            return new CommandLineArgs(genomFile, descriptionFile, resultFile, sequencesFile);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return null;
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        Option input = new Option("g", "genom", true, "Genom file path");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("d", "description", true, "Description file");
        output.setRequired(true);
        options.addOption(output);

        Option result = new Option("r", "result", true, "Result CSV file");
        result.setRequired(true);
        options.addOption(result);

        Option sequences = new Option("s", "sequences", true, "Sequences CSV file");
        sequences.setRequired(false);
        options.addOption(sequences);
        return options;
    }

    private record CommandLineArgs(String genomFile, String descriptionFile, String resultFile, String sequencesFile) {
    }
}
