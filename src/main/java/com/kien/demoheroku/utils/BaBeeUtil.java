package com.kien.demoheroku.utils;

import com.kien.demoheroku.entities.PhrasalVerb;
import com.kien.demoheroku.entities.Word;
import com.kien.demoheroku.entities.WordMask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaBeeUtil {

    public static final String ENTER = "ENTER_KEY";
    public static final String DOT = "DOT_KEY";

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

    public static List<String> getWordsFromSentence (String sentence) {
        sentence = BaBeeUtil.removeSpecialCharacters(sentence);
        String[] words = sentence.split(" ");

        List<String> result = new ArrayList<>();
        result.add(sentence);
        Collections.addAll(result, words);

        return result;
    }

    public static String removeSpecialCharacters (String text) {
        // remove possessive adjectives
        text = text.replaceAll("’s", "");
        text = text.replaceAll("'s", "");

        // remove special characters
        text = text.replaceAll("[!?()<>|.:,'“”\"]", "");

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
        sentence = sentence.substring(sentence.indexOf(verb) + verb.length());
        return sentence.contains(" " + preposition + " ");
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

    public static List<String> getSentencesListByText (String paragraph) {
        ArrayList<String> result_list = new ArrayList<>();

        String[] entries = paragraph.split("\n");

        for (String entry: entries) {
            if (entry.isEmpty()) result_list.add(ENTER);
            else {
                String[] sentences = entry.split("\\.");
                for (String sentence: sentences) {
                    if (sentence.isEmpty()) result_list.add(DOT);
                    else result_list.add(sentence);
                }
                result_list.add(ENTER);
            }

        }

        return result_list;
    }

    public static List<?> getListWordMaskFromSentences (List<String> sentencesList) {
        ArrayList<List<WordMask>> result_list = new ArrayList<>();

        for (String sentence : sentencesList) {
            if (sentence.equals(ENTER)) {
                result_list.add(null);
                continue;
            }

            List<WordMask> wordMasks = new ArrayList<>();
            String[] words = sentence.split(" ");
            for (String word : words) {
                WordMask wordMask = new WordMask();
                wordMask.setMask(word);
                wordMask.setWord(BaBeeUtil.removeSpecialCharacters(word));
                wordMasks.add(wordMask);
            }

            result_list.add(wordMasks);
        }

        return result_list;
    }

    public static String buildHTMLParagragh (List<List<WordMask>> list_main) {
        StringBuilder result = new StringBuilder();
        for (List<WordMask> wordMasks : list_main) {
            if (wordMasks == null) {
                result.append("<br>");
                continue;
            }
            StringBuffer sentence = new StringBuffer();
            for (WordMask wordMask : wordMasks) {
                if (wordMask.getWord() == null) {
                    sentence.append(wordMask.getMask()).append(" ");
                } else if (wordMask.getWord().isEmpty()) {
                    sentence.append(wordMask.getMask()).append(" ");
                } else {
                    sentence.append("<span style=\"background: #32acc5;\">").append(wordMask.getMask()).append("</span> ");
                }
            }
            sentence = new StringBuffer(sentence.toString().trim());
            if (wordMasks.size()>1) sentence.append(". ");
            result.append(sentence);
        }

        return result.toString();
    }

    public static void main(String[] args) {
//        String text = "Breaking the stigma\n" +
//                "\n" +
//                "Valentina publishes videos of Sirio doing things like going to school, while carrying a bag almost as big as he is. In another video, he is driving a play electric car. In another, he is waking up his older brother, Nilo.\n" +
//                "\n" +
//                "Sirio’s followers respond with a countless number of “likes” and messages of support.\n" +
//                "\n" +
//                "Valentina said she wanted to end the stigma – or unfair beliefs – that often surrounds disability. She wanted to show that children with special needs can still have fun and lead a happy life.\n" +
//                "\n" +
//                "“We understood that it is…necessary to talk about disability without (seeking) pity, different from the usual way disability is narrated,” she said.";

//        List<String> list = (List<String>) BaBeeUtil.getSentencesListByText(text);

//        for (String str: list) {
//            System.out.println(str);
//        }
//
//        List<?> list2 = BaBeeUtil.getListWordMaskFromSentences(list);

//        System.out.println(list2);
    }

}

