package com.artemis.kahn.dao.mongo;

import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.peri.AbstractMongoTemplate;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class JobDao extends AbstractMongoTemplate<Job, ObjectId> {
    @Override
    public Class<Job> getClasss() {
        return Job.class;
    }
}
