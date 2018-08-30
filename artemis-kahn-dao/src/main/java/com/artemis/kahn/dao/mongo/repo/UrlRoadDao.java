package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.UrlRoad;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import org.springframework.stereotype.Service;


@Service
public class UrlRoadDao extends MongoDaoImpl<UrlRoad> implements MongoDao<UrlRoad> {

}
