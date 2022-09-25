package org.microbiology.genom2;

import java.util.HashMap;
import java.util.Map;

public class GenomDto {
    private String code;
    private String ncNotation;

    private Map<String, String> props = new HashMap<>();

    public GenomDto(String code, String ncNotation) {
        this.code = code;
        this.ncNotation = ncNotation;
    }

    public void addProperty(String key, String value) {
        props.put(key, value);
    }

    public String getCode() {
        return code;
    }

    public String getNcNotation() {
        return ncNotation;
    }

    public Map<String, String> getProps() {
        return props;
    }
}
