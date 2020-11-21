package com.kien.demoheroku.entities;

import java.util.Date;

public class Word {

    private String word;
    private String type;
    private String meaning;
    private String level;
    private Date creationDate = new Date();

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
