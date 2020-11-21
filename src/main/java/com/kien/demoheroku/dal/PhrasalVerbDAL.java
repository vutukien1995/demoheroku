package com.kien.demoheroku.dal;



import com.kien.demoheroku.entities.PhrasalVerb;

import java.util.List;

public interface  PhrasalVerbDAL {

    List<PhrasalVerb> getByVerb (String verb);
    List<PhrasalVerb> getListByVerb (String verb);
    PhrasalVerb getByVerbAndPreposition (String verb, String preposition);

}
