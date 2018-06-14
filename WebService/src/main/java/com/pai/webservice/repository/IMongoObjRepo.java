package com.pai.webservice.repository;

import com.pai.webservice.model.MongoDbObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IMongoObjRepo extends MongoRepository<MongoDbObject, String> {
    List<MongoDbObject> findAllByConvId(String convId);
}
