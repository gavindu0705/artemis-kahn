package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.Urls;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import org.springframework.stereotype.Service;


@Service
public class UrlsDao extends MongoDaoImpl<Urls> implements MongoDao<Urls> {

}
