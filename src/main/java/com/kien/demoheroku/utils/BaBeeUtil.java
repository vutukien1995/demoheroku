package com.kien.demoheroku.utils;

import com.kien.demoheroku.entities.PhrasalVerb;
import com.kien.demoheroku.entities.Word;

import java.util.ArrayList;
import java.util.List;

public class BaBeeUtil {

    public static List<String> getListSentences (String paragraph) {
        ArrayList<String> list = new ArrayList<>();

        paragraph = paragraph.replaceAll("\n", ".");
        String[] sentences = paragraph.split("\\.");

        for (String sentence : sentences) {
            sentence = removeSpecialCharacters(sentence);
            list.add(sentence);
        }

        return list;
    }

    public static String removeSpecialCharacters (String text) {
        // remove possessive adjectives
        text = text.replaceAll("’s","");
        text = text.replaceAll("'s","");

        // remove special characters
        text = text.replaceAll("[!?()<>||-–.:,'“”\"]","");

        text = text.trim().toLowerCase();

        // remove number
        text = text.replaceAll("[0-9]", "");

        return text;
    }

    public static List<PhrasalVerb> getPhrasalVerbFromText (String text) {
        List<PhrasalVerb> pvLst = new ArrayList<>();
        String[] pvlstContainer = text.split("\n");

        for (String pvtxt : pvlstContainer) {
            PhrasalVerb phrasalVerb = new PhrasalVerb();
            String[] phrasalVerbContainer = pvtxt.split("/");
            String[] phrasalVerbContainer2 = new String[5];
            for(int i = 0; i<5; i++) {
                if (i<phrasalVerbContainer.length) {
                    phrasalVerbContainer2[i] = phrasalVerbContainer[i];
                } else {
                    phrasalVerbContainer2[i] = "";
                }
            }
            phrasalVerb.setVerb(phrasalVerbContainer2[0]);
            phrasalVerb.setPreposition(phrasalVerbContainer2[1]);
            phrasalVerb.setDisplay(phrasalVerbContainer2[2]);
            phrasalVerb.setDefinition(phrasalVerbContainer2[3]);
            phrasalVerb.setExample(phrasalVerbContainer2[4]);

            pvLst.add(phrasalVerb);
        }

        return pvLst;
    }

    public static boolean checkPhrasalVerbInSentence (String verb, String preposition, String sentence) {
        // format
        verb = verb.toLowerCase();
        preposition = preposition.toLowerCase();
        sentence =sentence.toLowerCase();

        // check contain
        if (!(sentence.contains(verb) && sentence.contains(preposition))) {
            return false;
        }

        // check phrasal verb in sentence
        sentence = sentence.substring(sentence.indexOf(verb) + verb.length(), sentence.length());
        return sentence.contains(" " + preposition + " ");
    }

    public static List<String> getWordsFromSentence (String sentence) {
        sentence = BaBeeUtil.removeSpecialCharacters(sentence);
        String[] words = sentence.split(" ");

        List<String> result = new ArrayList<>();
        result.add(sentence);
        for (String word: words) {
            result.add(word);
        }

        return result;
    }

    public static List<Word> getWordFromText (String text) {
        List<Word> wordLst = new ArrayList<>();
        String[] wordlstContainer = text.split("\n");

        for (String wordTxt : wordlstContainer) {
            Word word = new Word();
            String[] wordContainer = wordTxt.split("/");
            String[] wordContainer2 = new String[3];
            for(int i = 0; i<3; i++) {
                if (i<wordContainer.length) {
                    wordContainer2[i] = wordContainer[i];
                } else {
                    wordContainer2[i] = "";
                }
            }
            word.setWord(wordContainer2[0].trim().toLowerCase());
            word.setType(wordContainer2[1].trim());
            word.setMeaning(wordContainer2[2].trim());

            wordLst.add(word);
        }

        return wordLst;
    }

    public static void main(String[] args) {
        String text = "home/n/Nhà";

        List<Word> words = BaBeeUtil.getWordFromText(text);

        for (Word word: words) {
            System.out.println(word);
        }

    }

}

