package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.SiteConfig;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;


@Service
public class SiteConfigDao extends MongoDaoImpl<SiteConfig> implements MongoDao<SiteConfig> {

	public SiteConfig findSiteConfigByRoot(String root) {
		DBObject q = new BasicDBObject();
		q.put("root", root);
		return this.findOne(q);
	}
	
}
