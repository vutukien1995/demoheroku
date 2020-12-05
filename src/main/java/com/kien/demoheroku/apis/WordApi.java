package com.kien.demoheroku.apis;


import com.kien.demoheroku.dalimpl.WordDALImpl;
import com.kien.demoheroku.dto.WordListDTO;
import com.kien.demoheroku.entities.Word;
import com.kien.demoheroku.repositories.WordRepository;
import com.kien.demoheroku.utils.BaBeeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/words")
public class WordApi {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final WordRepository wordRepository;
    private final WordDALImpl wordDALImpl;

    public WordApi(WordRepository wordRepository, WordDALImpl wordDALImpl) {
        this.wordRepository = wordRepository;
        this.wordDALImpl = wordDALImpl;
    }

    @GetMapping("/getAll")
    public List<Word> getAll () {
        LOG.info("Call /word/getAll");

        return wordRepository.findAll();
    }

    @PostMapping("/create_by_list_text")
    public String createByText (@ModelAttribute WordListDTO wordListDTO) {
        LOG.info("Word create_by_list_text.");

        List<Word> words = BaBeeUtil.getWordFromText(wordListDTO.getText());
        int count = 0;
        for (Word word: words) {
            word.setGroup(wordListDTO.getGroup());
            wordRepository.save(word);
            LOG.info("Save word: " + word);
            count++;
        }

        return "Create " + count + " new words successfully.";
    }

    @GetMapping("/get_list_by_group")
    public List<Word> getListByGroup (@RequestParam(value="content", required=false, defaultValue = "") String group) {
        LOG.info("Call /word/get_list_by_group");

        return wordDALImpl.getListByGroup(group);
    }
}
