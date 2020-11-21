package com.kien.demoheroku.repositories;

import com.kien.demoheroku.entities.Contribute;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributeRepository extends MongoRepository<Contribute, String> {

    Contribute findByid(ObjectId id);

}
