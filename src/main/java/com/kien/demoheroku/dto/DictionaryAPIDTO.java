package com.kien.demoheroku.dto;

public class DictionaryAPIDTO {

    private String word;
    private Phonetic[] phonetics;
    private Meaning[] meanings;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Phonetic[] getPhonetics() {
        return phonetics;
    }

    public void setPhonetics(Phonetic[] phonetics) {
        this.phonetics = phonetics;
    }

    public Meaning[] getMeanings() {
        return meanings;
    }

    public void setMeanings(Meaning[] meanings) {
        this.meanings = meanings;
    }
}
