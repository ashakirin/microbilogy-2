package org.microbiology.genom2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescriptionFileParser {
    private static Pattern PROTEIN_ID_PATTERN = Pattern.compile("\\|([^|]+)\\|");


    public Map<String, DescriptionDto> parseDescriptionFile(String filePath) {
        Map<String, DescriptionDto> result = new HashMap<>();
        try {
            String content = Files.readString(Path.of(filePath));
            List<String> genoms = (List<String>) Arrays.asList(content.split("\tgene\n"));
            for (String genom : genoms) {
                String xref = extractAttribute(genom, "\t\t\tdb_xref");
                String proteinId = parseProteinId(extractAttribute(genom, "\t\t\tprotein_id"));

                if (xref == null && proteinId == null) {
                    continue;
                }
                String gene = extractAttribute(genom, "\t\t\tgene");
                String locusTag = extractAttribute(genom, "\t\t\tlocus_tag");
                String product = extractAttribute(genom, "\t\t\tproduct\t");
                String note = extractAttribute(genom, "\t\t\tnote");
                DescriptionDto descriptionDto = new DescriptionDto(xref);
                descriptionDto.setGene(gene);
                descriptionDto.setLocusTag(locusTag);
                descriptionDto.setNote(note);
                descriptionDto.setProduct(product);
                if (xref != null) {
                    result.put(xref, descriptionDto);
                }
                if (proteinId != null) {
                    result.put(proteinId, descriptionDto);
                }
            }
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read input description file: " + e.getMessage(), e);
        }

    }

    private String parseProteinId(String proteinIdValue) {
        if (proteinIdValue == null) {
            return null;
        }
        Matcher matcher = PROTEIN_ID_PATTERN.matcher(proteinIdValue);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return proteinIdValue;
        }
    }

    private String extractAttribute(String genom, String attrName) {
        int xrefBegin = genom.indexOf(attrName);
        if (xrefBegin == -1) {
            return null;
        }
        int xrefEnd = genom.indexOf("\n", xrefBegin);
        return genom.substring(xrefBegin + attrName.length(), xrefEnd).trim().replaceAll(",", "");
    }

}
