package org.microbiology.genom2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescriptionFileParser {

    public Map<String, DescriptionDto> parseDescriptionFile(String filePath) {
        Map<String, DescriptionDto> result = new HashMap<>();
        try {
            String content = Files.readString(Path.of(filePath));
            List<String> genoms = (List<String>) Arrays.asList(content.split("\tgene\n"));
            for (String genom : genoms) {
                String xref = extractAttribute(genom, "\t\t\tdb_xref");
                if (xref == null) {
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
                result.put(xref, descriptionDto);
            }
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read input description file: " + e.getMessage(), e);
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
