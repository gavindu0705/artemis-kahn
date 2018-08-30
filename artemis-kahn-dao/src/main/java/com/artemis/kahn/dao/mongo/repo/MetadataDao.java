package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.Metadata;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import org.springframework.stereotype.Service;


@Service
public class MetadataDao extends MongoDaoImpl<Metadata> implements MongoDao<Metadata> {

}
