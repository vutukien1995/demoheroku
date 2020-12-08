package com.kien.demoheroku.utils;

import com.google.gson.Gson;
import com.kien.demoheroku.dto.Definition;
import com.kien.demoheroku.dto.DictionaryAPIDTO;
import com.kien.demoheroku.dto.Meaning;
import com.kien.demoheroku.dto.Phonetic;
import com.kien.demoheroku.entities.Word;
import com.kien.demoheroku.entities.WordMask;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HttpClient implements Callable {

    private WordMask wordMask;

    public HttpClient (WordMask wordMask) {
        this.wordMask = wordMask;
    }

    public static DictionaryAPIDTO[] getWordFromText (String text) {
        HttpResponse<String> response = Unirest.get("https://api.dictionaryapi.dev/api/v2/entries/en/{word}")
                .routeParam("word", text)
                .asString();

        try {
            Gson gson = new Gson();
            DictionaryAPIDTO[] words = gson.fromJson(response.getBody(), DictionaryAPIDTO[].class);

            if (words.length == 0) words = null;
            System.out.println("[Info] " + text + " -> " + words);

            return words;
        } catch (Exception e) {
            System.out.println("[Error] " + text + " -> " + e.getMessage());
            return null;
        }
    }

    public static List<Word> getWordStandardFromText (String text) {
        HttpResponse<String> response = Unirest.get("https://api.dictionaryapi.dev/api/v2/entries/en/{word}")
                .routeParam("word", text)
                .asString();

        try {
            List<Word> result_word = new ArrayList<>();
            Gson gson = new Gson();
            DictionaryAPIDTO[] words = gson.fromJson(response.getBody(), DictionaryAPIDTO[].class);

            if (words.length == 0) return result_word;

            for (DictionaryAPIDTO word : words) {
                Phonetic[] phonetics = word.getPhonetics();
                Meaning[] meanings = word.getMeanings();

                for (Meaning meaning: meanings) {
                    Word r_word = new Word();
                    r_word.setWord(word.getWord());
                    r_word.setType(meaning.getPartOfSpeech());
                    r_word.setPronunciation(phonetics.length>0 ? phonetics[0].getText() : "");
                    r_word.setAudio(phonetics.length>0 ? phonetics[0].getAudio() : "");
                    r_word.setText(phonetics.length>0 ? phonetics[0].getText() : "");
                    for (Definition definition: meaning.getDefinitions()) {
                        if (r_word.getMeaning() == null)
                            r_word.setMeaning(definition.getDefinition() + "<br><br>");
                        else
                            r_word.setMeaning(r_word.getMeaning() + definition.getDefinition() + "<br><br>");
                    }
                    r_word.setMeaning(r_word.getMeaning().substring(0, r_word.getMeaning().length()-8));

                    result_word.add(r_word);
                }
            }

            System.out.println("[Info] " + text + " -> " + result_word.get(0).getWord());
            return result_word;
        } catch (Exception e) {
            System.out.println("[Error] " + text + " -> " + e.getStackTrace());
            return null;
        }
    }

    @Override
    public WordMask call() {
        List<Word> words = getWordStandardFromText(wordMask.getWord());
        if (words == null || words.size() == 0) {
            this.wordMask.setWord(null);
        } else {
            this.wordMask.setWords(words);
            this.wordMask.setWord(this.wordMask.getWords().get(0).getWord());
        }
        return this.wordMask;
    }

    public WordMask getWordMask() {
        return wordMask;
    }

    public void setWordMask(WordMask wordMask) {
        this.wordMask = wordMask;
    }

    public static void main(String[] args) {
//        System.out.println("check:" + HttpClient.getInfinitiveWordFromText("finds"));

    }

}