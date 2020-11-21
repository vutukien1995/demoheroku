package com.kien.demoheroku.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class PhrasalVerb {

    @Id
    private String id;
    private String verb;
    private String preposition;
    private String display;
    private String definition;
    private String example;
    private Date creationDate = new Date();
    private String contributor;

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getPreposition() {
        return preposition;
    }

    public void setPreposition(String preposition) {
        this.preposition = preposition;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

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

    @Override
    public String toString() {
        return "PhrasalVerb{" +
                "id='" + id + '\'' +
                ", verb='" + verb + '\'' +
                ", preposition='" + preposition + '\'' +
                ", display='" + display + '\'' +
                ", definition='" + definition + '\'' +
                ", example='" + example + '\'' +
                ", creationDate=" + creationDate +
                ", contributor='" + contributor + '\'' +
                '}';
    }
}
