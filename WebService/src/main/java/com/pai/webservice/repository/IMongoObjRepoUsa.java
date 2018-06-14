package com.pai.webservice.repository;

import com.pai.webservice.model.MongoDbObject;
import com.pai.webservice.model.MongoDbObjectUsa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IMongoObjRepoUsa extends MongoRepository<MongoDbObjectUsa, String> {
    List<MongoDbObjectUsa> findAllByConvId(String convId);
}
