package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.Page;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PageDao extends MongoDaoImpl<Page> implements MongoDao<Page> {

	public List<Page> findPageByJobId(String jobId) {
		return this.findAll(new BasicDBObject("job_id", jobId), new BasicDBObject("c_date", 1));
	}

//	public void updatePageStop(String id) {
//		DBObject data = new BasicDBObject();
//		data.put("status", Page.TERMINAL);
//		this.updateById(data, id);
//	}
//
//	public void updatePageNormalByJobId(String jobId) {
//		DBObject data = new BasicDBObject();
//		data.put("status", Page.NORMAL);
//		this.update(data, new BasicDBObject("job_id", jobId));
//	}

	public void deleteByJobId(String jobId) {
		DBObject q = new BasicDBObject();
		q.put("job_id", jobId);
		this.delete(q);
	}

}
