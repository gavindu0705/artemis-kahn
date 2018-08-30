package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.Job;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import org.springframework.stereotype.Service;

@Service
public class JobDao extends MongoDaoImpl<Job> implements MongoDao<Job> {

}
