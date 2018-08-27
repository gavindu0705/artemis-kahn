package com.artemis.kahn.dao.mongo;

import com.artemis.kahn.dao.mongo.entity.Task;
import com.artemis.kahn.dao.mongo.peri.AbstractMongoTemplate;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRepo extends AbstractMongoTemplate<Task, ObjectId> {
    @Override
    public Class<Task> getClasss() {
        return Task.class;
    }
}
