package com.kien.demoheroku.controllers;


import com.kien.demoheroku.dalimpl.PhrasalVerbDALImpl;
import com.kien.demoheroku.entities.*;
import com.kien.demoheroku.repositories.ContributeRepository;
import com.kien.demoheroku.repositories.MostCommonWordRepository;
import com.kien.demoheroku.repositories.WordRepository;
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
import java.util.List;

@RestController
public class MainController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final MostCommonWordRepository mostCommonWordRepository;
    private final ContributeRepository contributeRepository;
    private final PhrasalVerbDALImpl phrasalVerbDAL;
    private final WordRepository wordRepository;

    public MainController (WordRepository wordRepository,
                           PhrasalVerbDALImpl phrasalVerbDAL,
                           MostCommonWordRepository mostCommonWordRepository,
                           ContributeRepository contributeRepository) {
        this.phrasalVerbDAL = phrasalVerbDAL;
        this.mostCommonWordRepository = mostCommonWordRepository;
        this.contributeRepository = contributeRepository;
        this.wordRepository = wordRepository;
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

        // Catch unknown words
        List<Word> listWord = new ArrayList<>();
        List<MostCommonWord> allCommonWordsList = mostCommonWordRepository.findAll();
        for (String sentence : listSentence) {
            String[] listWordInSentence = sentence.split(" ");
            for (String word: listWordInSentence) {
                if (word.isEmpty() || word.length() == 1) continue;

                MostCommonWord commonWord = allCommonWordsList.stream()
                        .filter(element -> word.equals(element.getWord()))
                        .findAny().orElse(null);
                if (commonWord == null) {
                    Word word_to_view = new Word();
                    word_to_view.setWord(word);
                    word_to_view.setType("n");
                    word_to_view.setMeaning("what is mean?");
                    listWord.add(word_to_view);
                }
            }
        }

        // Catch phrasal verbs
        List<PhrasalVerb> phrasalVerbLst = new ArrayList<>();
        for (String sentence : listSentence) {
            // Words from sentence
            List<String> words = BaBeeUtil.getWordsFromSentence(sentence);
            for (int i = 1; i<words.size(); i++) {
                List<PhrasalVerb> pvList = phrasalVerbDAL.getListByVerb(words.get(i));
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
        if (!content.isEmpty()) paragraphContent = BaBeeUtil.buildHTMLParagragh((List<List<WordMask>>) BaBeeUtil.getListWordMaskFromSentences((List<String>) BaBeeUtil.getSentencesListByText(content)),
                listWord);

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
