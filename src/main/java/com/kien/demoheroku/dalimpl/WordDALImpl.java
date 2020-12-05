package com.kien.demoheroku.dalimpl;

import com.kien.demoheroku.dal.WordDAL;
import com.kien.demoheroku.entities.PhrasalVerb;
import com.kien.demoheroku.entities.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WordDALImpl implements WordDAL {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Word> getListByGroup(String group) {
        Query query = new Query();
        query.addCriteria(Criteria.where("group").is(group));
        return mongoTemplate.find(query, Word.class);
    }

}
