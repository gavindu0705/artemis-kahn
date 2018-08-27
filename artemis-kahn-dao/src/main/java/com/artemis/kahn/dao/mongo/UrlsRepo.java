package com.artemis.kahn.dao.mongo;

import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.dao.mongo.peri.AbstractMongoTemplate;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class UrlsRepo extends AbstractMongoTemplate<Urls, ObjectId> {

    @Override
    public Class<Urls> getClasss() {
        return Urls.class;
    }
}
