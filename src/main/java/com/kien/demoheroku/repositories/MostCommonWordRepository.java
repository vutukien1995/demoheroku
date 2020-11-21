package com.kien.demoheroku.repositories;

import com.kien.demoheroku.entities.MostCommonWord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MostCommonWordRepository extends MongoRepository<MostCommonWord, String> {
}
