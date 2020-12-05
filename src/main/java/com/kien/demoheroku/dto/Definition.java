package com.kien.demoheroku.dto;

public class Definition {

    private String definition;
    private String example;
    private Object synonyms;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public Object getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Object synonyms) {
        this.synonyms = synonyms;
    }
}
