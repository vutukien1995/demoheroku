package com.kien.demoheroku.apis;

import com.kien.demoheroku.dalimpl.PhrasalVerbDALImpl;
import com.kien.demoheroku.dto.ParagraphDTO;
import com.kien.demoheroku.entities.PhrasalVerb;
import com.kien.demoheroku.repositories.PhrasalVerbRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping(path = "/phrasal_verb")
public class PhrasalVerbApi {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final PhrasalVerbRepository phrasalVerbRepository;
    private final PhrasalVerbDALImpl phaPhrasalVerbDALImpl;

    public PhrasalVerbApi(PhrasalVerbRepository phrasalVerbRepository,
                          PhrasalVerbDALImpl phaPhrasalVerbDALImpl) {
        this.phrasalVerbRepository = phrasalVerbRepository;
        this.phaPhrasalVerbDALImpl = phaPhrasalVerbDALImpl;
    }

    @PostMapping("/create")
    public PhrasalVerb addNew (@RequestBody PhrasalVerb phrasalVerb) {
        LOG.info("Saving a new PhrasalVerb.");

        // format data
        phrasalVerb.setVerb(phrasalVerb.getVerb().trim().toLowerCase());
        phrasalVerb.setPreposition(phrasalVerb.getPreposition().trim().toLowerCase());

        return phrasalVerbRepository.save(phrasalVerb);
    }

    @GetMapping("/getAll")
    public List<PhrasalVerb> getAllUsers() {
        LOG.info("Getting all PhrasalVerb.");
        return phrasalVerbRepository.findAll();
    }

    @PostMapping("/doCatchWords")
    public ModelAndView getAllUsers(@ModelAttribute("paragraphDTO") ParagraphDTO paragraphDTO) {
        LOG.info("Call get doCatchWords.");
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("content", paragraphDTO.getContent());
        return new ModelAndView("redirect:/catch-the-words", modelMap);
    }

    @GetMapping("/getByVerb")
    public List<PhrasalVerb> getByVerb(@RequestParam(value="verb", required=false, defaultValue = "") String verb) {
        LOG.info("Getting all PhrasalVerb.");
        return (List<PhrasalVerb>) phaPhrasalVerbDALImpl.getByVerb(verb);
    }

    @GetMapping("/createByList")
    public String addNewByList (@RequestParam(value="text", required=false, defaultValue = "") String text) {
        LOG.info("Saving a new PhrasalVerb.");

        String[] lines = text.split("/r");

        return "hehe";
    }
}
