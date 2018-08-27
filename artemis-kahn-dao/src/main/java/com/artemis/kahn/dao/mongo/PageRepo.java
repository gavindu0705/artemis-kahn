package com.artemis.kahn.dao.mongo;

import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.dao.mongo.peri.AbstractMongoTemplate;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class PageRepo extends AbstractMongoTemplate<Page, ObjectId> {
    @Override
    public Class<Page> getClasss() {
        return Page.class;
    }
}
