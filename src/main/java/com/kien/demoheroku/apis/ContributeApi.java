package com.kien.demoheroku.apis;


import com.kien.demoheroku.entities.Contribute;
import com.kien.demoheroku.entities.PhrasalVerb;
import com.kien.demoheroku.repositories.ContributeRepository;
import com.kien.demoheroku.repositories.PhrasalVerbRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.kien.demoheroku.utils.BaBeeUtil;

import java.util.List;

@RestController
@RequestMapping(path = "/contribute")
public class ContributeApi {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final ContributeRepository contributeRepository;
    private final PhrasalVerbRepository phrasalVerbRepository;

    ContributeApi (ContributeRepository contributeRepository,
                   PhrasalVerbRepository phrasalVerbRepository) {
        this.contributeRepository = contributeRepository;
        this.phrasalVerbRepository = phrasalVerbRepository;
    }

    @GetMapping("/getAll")
    public List<Contribute> getAll() {
        LOG.info("Contribute get all.");
        return contributeRepository.findAll();
    }

    @PostMapping("/saveContribute")
    public ModelAndView saveContribute (@ModelAttribute Contribute contribute) {
        LOG.info("Contribute save new.");

        contribute.setType("PHRASAL_VERB");
        contributeRepository.save(contribute);

        ModelMap modelMap = new ModelMap();
        return new ModelAndView("redirect:/", modelMap);
    }

    @GetMapping("confirmContribute")
    public String confirmContribute (@RequestParam(value="id", required=false, defaultValue = "") String id) {
        LOG.info("Contribute confirming.");

        Contribute contribute = contributeRepository.findByid(new ObjectId(id));
        List<PhrasalVerb> phrasalVerbList = BaBeeUtil.getPhrasalVerbFromText(contribute.getContent());
        for(PhrasalVerb phrasalVerb: phrasalVerbList) {
            // format data
            phrasalVerb.setVerb(phrasalVerb.getVerb().trim().toLowerCase());
            phrasalVerb.setPreposition(phrasalVerb.getPreposition().trim().toLowerCase());

            phrasalVerb.setContributor(contribute.getContributor());
            phrasalVerbRepository.save(phrasalVerb);
        }

        contribute.setStatus("CONFIRMED");
        contributeRepository.save(contribute);

        return "Contribute is confirmed successfully";
    }

}
