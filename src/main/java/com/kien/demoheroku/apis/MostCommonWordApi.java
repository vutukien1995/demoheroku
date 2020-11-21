package com.kien.demoheroku.apis;

import com.kien.demoheroku.dto.CommonWordsListDTO;
import com.kien.demoheroku.entities.MostCommonWord;
import com.kien.demoheroku.repositories.MostCommonWordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/common_words")
public class MostCommonWordApi {
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final MostCommonWordRepository mostCommonWordRepository;

    public MostCommonWordApi(MostCommonWordRepository mostCommonWordRepository) {
        this.mostCommonWordRepository = mostCommonWordRepository;
    }

    @PostMapping("/create")
    public MostCommonWord addNew (@RequestBody MostCommonWord mostCommonWord) {
        LOG.info("Saving a new MostCommonWord.");
        return mostCommonWordRepository.save(mostCommonWord);
    }

    @GetMapping("/getAll")
    public List<MostCommonWord> getAll () {
        LOG.info("Get all MostCommonWord.");
        return mostCommonWordRepository.findAll();
    }

    @PostMapping("/create_by_list_text")
    public String createByListText (@RequestBody CommonWordsListDTO commonWordsListDTO) {
        LOG.info("Get by list text MostCommonWord.");

        ArrayList<String> list = new ArrayList<>();
        String[] words = commonWordsListDTO.getText().split(",");
        int count = 0;
        for (String word: words) {
            MostCommonWord commonWord = new MostCommonWord();
            commonWord.setLevel(commonWordsListDTO.getLevel());
            commonWord.setWord(word.trim());
            mostCommonWordRepository.save(commonWord);
            count++;
        }

        return "Created successfully " + count + " common words!";
    }

}
