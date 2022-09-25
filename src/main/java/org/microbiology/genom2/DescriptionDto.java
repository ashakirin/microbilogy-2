package org.microbiology.genom2;

public class DescriptionDto {

    private String xref;
    private String gene;
    private String locusTag;
    private String note;
    private String product;

    public DescriptionDto(String xref) {
        this.xref = xref;
    }

    public String getLocusTag() {
        return locusTag;
    }

    public void setLocusTag(String locusTag) {
        this.locusTag = locusTag;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getXref() {
        return xref;
    }
}
