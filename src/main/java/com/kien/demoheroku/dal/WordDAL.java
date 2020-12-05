package com.kien.demoheroku.dal;

import com.kien.demoheroku.entities.Word;

import java.util.List;

public interface WordDAL {

    List<Word> getListByGroup(String group);

}
