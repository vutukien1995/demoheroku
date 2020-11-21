package com.kien.demoheroku.repositories;

import com.kien.demoheroku.entities.PhrasalVerb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhrasalVerbRepository extends MongoRepository<PhrasalVerb, String> {
}

