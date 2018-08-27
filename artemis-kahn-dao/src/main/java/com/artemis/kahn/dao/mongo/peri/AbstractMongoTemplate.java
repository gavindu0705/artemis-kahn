package com.artemis.kahn.dao.mongo.peri;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class AbstractMongoTemplate<E, K> {

    public abstract Class<E> getClasss();

    @Autowired
    protected MongoTemplate mongoTemplate;

    public void save(E entity) {
        mongoTemplate.save(entity);
    }

    public E findById(K id) {
        return mongoTemplate.findById(id, getClasss());
    }

    public List<E> findAll() {
        return mongoTemplate.findAll(getClasss());
    }

    public List<E> find(Query query) {
        return mongoTemplate.find(query, getClasss());
    }

    public DeleteResult removeById(K id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        return mongoTemplate.remove(query);
    }

    public UpdateResult upsert(Query query, Update update) {
        return mongoTemplate.upsert(query, update, getClasss());
    }


}
