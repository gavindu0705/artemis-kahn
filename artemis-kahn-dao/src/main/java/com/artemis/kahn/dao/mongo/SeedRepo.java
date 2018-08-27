package com.artemis.kahn.dao.mongo;

import com.artemis.kahn.dao.mongo.entity.Seed;
import com.artemis.kahn.dao.mongo.peri.AbstractMongoTemplate;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class SeedRepo extends AbstractMongoTemplate<Seed, ObjectId> {
    @Override
    public Class<Seed> getClasss() {
        return Seed.class;
    }
}
