package com.kien.demoheroku.controllers;


import com.kien.demoheroku.entities.*;
import com.kien.demoheroku.repositories.ContributeRepository;
import com.kien.demoheroku.repositories.MostCommonWordRepository;
import com.kien.demoheroku.repositories.PhrasalVerbRepository;
import com.kien.demoheroku.repositories.WordRepository;
import com.kien.demoheroku.utils.HttpClient;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.kien.demoheroku.utils.BaBeeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@RestController
public class MainController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final int THREAD_NUMBER = 26;

    private final MostCommonWordRepository mostCommonWordRepository;
    private final ContributeRepository contributeRepository;
    private final WordRepository wordRepository;
    private final PhrasalVerbRepository phrasalVerbRepository;

    public MainController(WordRepository wordRepository,
                          MostCommonWordRepository mostCommonWordRepository,
                          ContributeRepository contributeRepository,
                          PhrasalVerbRepository phrasalVerbRepository) {
        this.mostCommonWordRepository = mostCommonWordRepository;
        this.contributeRepository = contributeRepository;
        this.wordRepository = wordRepository;
        this.phrasalVerbRepository = phrasalVerbRepository;
    }

    @GetMapping("/")
    public ModelAndView index() {
        LOG.info("Index ");

        List<Contribute> contributeLst = contributeRepository.findAll(Sort.by(Sort.Direction.DESC, "creationDate"));

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Home");
        modelAndView.addObject("contributeLst", contributeLst);
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @GetMapping("/test")
    public ModelAndView test() {
        LOG.info("Index ");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Test");
        modelAndView.setViewName("test");
        return modelAndView;
    }

    @GetMapping("/catch-the-words")
    public ModelAndView catchTheWords(@RequestParam(value="content", required=false, defaultValue = "") String content) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("content", content);
        modelAndView.addObject("title", "Catch Words");
        modelAndView.addObject("background_image", "img/post-bg.jpg");
        modelAndView.setViewName("catchWords");

        // Get list sentences from paragraph
        List<String> listSentence = BaBeeUtil.getListSentences(content);

        // Catch words with api
        List<List<WordMask>> listWordMask = (List<List<WordMask>>) BaBeeUtil.getListWordMaskFromSentences(BaBeeUtil.getSentencesListByText(content));

        // Get list common word
        List<MostCommonWord> allCommonWordsList = mostCommonWordRepository.findAll();

        // Standardize word to infinitive
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);
        List<Future<WordMask>> futureList = new ArrayList<>();
        for (List<WordMask> wordMasks : listWordMask) {
            if (wordMasks != null)
                for (WordMask wordMask : wordMasks) {
                    // Word of wordMask is empty => next
                    if (wordMask.getWord().isEmpty()) {
                        continue;
                    }

                    // Check in common words
                    MostCommonWord commonWord = allCommonWordsList.stream()
                            .filter(element -> wordMask.getWord().equals(element.getWord()))
                            .findAny().orElse(null);
                    if (commonWord != null || wordMask.getWord().length() == 1) {
                        wordMask.setWord(null);
                    } else {
                        Callable worker = new HttpClient(wordMask);
                        Future future = executor.submit(worker);
                        futureList.add(future);
                    }
                }
        }

        // Catch unknown words v2
        List<Word> listWord = new ArrayList<>();
        for (Future<WordMask> future : futureList) {
            try {
                WordMask wordMask = future.get();

                // Check in common words
                MostCommonWord commonWord = allCommonWordsList.stream()
                        .filter(element -> wordMask.getWord().equals(element.getWord()))
                        .findAny().orElse(null);
                if (commonWord != null)
                    wordMask.setWord(null);

                if (wordMask.getWord() != null) {
                    Word duplicateWord =  listWord.stream()
                            .filter(element -> wordMask.getWord().equals(element.getWord()))
                            .findAny().orElse(null);
                    if (duplicateWord == null) {
                        listWord.addAll(wordMask.getWords());
                    }
                }
            } catch (Exception e) {
                LOG.error("Catch word v2 error message " + e.getMessage());
            }
        }
        executor.shutdown();
        // Sort list of words
        Collections.sort(listWord, (w1, w2) -> {
            return w1.getWord().compareTo(w2.getWord());
        });

        // Catch phrasal verbs
        List<PhrasalVerb> phrasalVerbLst = new ArrayList<>();
        List<PhrasalVerb> listAllPhrasalVerb = phrasalVerbRepository.findAll();
        for (String sentence : listSentence) {
            // Words from sentence
            List<String> words = BaBeeUtil.getWordsFromSentence(sentence);
            for (int i = 1; i<words.size(); i++) {
                String word = words.get(i);
                List<PhrasalVerb> pvList = listAllPhrasalVerb.stream()
                        .filter(element -> word.equals(element.getVerb()))
                        .collect(Collectors.toList());

                if (pvList.size()>0) {
                    // Check phrasal verb have in sentence
                    for (PhrasalVerb pv : pvList) {
                        if (BaBeeUtil.checkPhrasalVerbInSentence(pv.getVerb(), pv.getPreposition(), words.get(0))) {
                            phrasalVerbLst.add(pv);
                        }
                    }
                }
            }
        }

        // Build content paragraph
        String paragraphContent = "";
        if (!content.isEmpty()) paragraphContent = BaBeeUtil.buildHTMLParagragh(listWordMask);

        modelAndView.addObject("phrasalVerbLst", phrasalVerbLst);
        modelAndView.addObject("wordLst", listWord);
        modelAndView.addObject("paragraphContent", paragraphContent);

        return modelAndView;
    }

    @GetMapping("/phrasal-verbs")
    public ModelAndView phrasal_verbs() {
        LOG.info("Call phrasal-verbs");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Phrasal Verb");
        modelAndView.setViewName("phrasalVerbs");
        return modelAndView;
    }

    @GetMapping("/contribute-phrasal-verbs")
    public ModelAndView contribute_phrasal_verbs() {
        LOG.info("Call contribute-phrasal-verbs ");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Contribute Phrasal Verbs");
        modelAndView.setViewName("contributePhrasalVerbs");
        return modelAndView;
    }

    @GetMapping("/contribute-phrasal-verbs-view")
    public ModelAndView contribute_phrasal_verbs_view(@RequestParam(value="contribute", required=false, defaultValue = "") String contribute) {
        LOG.info("Call contribute-phrasal-verbs-view ");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Contribute Phrasal Verbs");
        modelAndView.setViewName("contributePhrasalVerbsView");

        // Get contribute
        Contribute contributeElement = contributeRepository.findByid(new ObjectId(contribute));
        System.out.println("contributeElement: " + contributeElement);
        // Phrasal verb for table
        List<PhrasalVerb> phrasalVerbLst = BaBeeUtil.getPhrasalVerbFromText(contributeElement.getContent());

        modelAndView.addObject("contribute", contributeElement);
        modelAndView.addObject("phrasalVerbLst", phrasalVerbLst);

        return modelAndView;
    }

    @GetMapping("/contribute-phrasal-verbs-view-admin")
    public ModelAndView contribute_phrasal_verbs_view_admin(@RequestParam(value="contribute", required=false, defaultValue = "") String contribute) {
        LOG.info("Call contribute-phrasal-verbs-view ");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Contribute Phrasal Verbs");
        modelAndView.setViewName("contributePhrasalVerbsViewAdmin");

        // Get contribute
        Contribute contributeElement = contributeRepository.findByid(new ObjectId(contribute));
        System.out.println("contributeElement: " + contributeElement);
        // Phrasal verb for table
        List<PhrasalVerb> phrasalVerbLst = BaBeeUtil.getPhrasalVerbFromText(contributeElement.getContent());

        modelAndView.addObject("contribute", contributeElement);
        modelAndView.addObject("phrasalVerbLst", phrasalVerbLst);

        return modelAndView;
    }

    @GetMapping("/about-us")
    public ModelAndView aboutUs () {
        LOG.info("Index ");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "About us");
        modelAndView.addObject("background_image", "img/about-bg.jpg");
        modelAndView.setViewName("about");
        return modelAndView;
    }

    @GetMapping("/vocabulary")
    public ModelAndView vocabulary () {
        LOG.info("Index ");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Vocabulary");
        modelAndView.addObject("background_image", "img/post-sample-image.jpg");
        modelAndView.setViewName("vocabulary");

        List<Word> wordList = wordRepository.findAll();
        modelAndView.addObject("wordList", wordList);

        return modelAndView;
    }

    @GetMapping("/vocabulary-admin")
    public ModelAndView vocabulary_admin () {
        LOG.info("Vocabulary admin ");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Vocabulary");
        modelAndView.addObject("background_image", "img/post-sample-image.jpg");
        modelAndView.setViewName("vocabularyAdmin");

        return modelAndView;
    }

    public static void main(String[] args) {
        String str= "This#string-contains^s'^pecial*characters& Country: How One Family’s\".";
        str = str.replaceAll("’s","");
        str = str.replaceAll("'s","");
        str = str.replaceAll("[:,\"]","");
        System.out.println(str);
    }

}
