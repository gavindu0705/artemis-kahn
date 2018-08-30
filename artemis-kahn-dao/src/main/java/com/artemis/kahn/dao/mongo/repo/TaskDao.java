package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.Task;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TaskDao extends MongoDaoImpl<Task> implements MongoDao<Task> {

	public List<Task> findTaskByJobPageId(String jobId, String pageId) {
		DBObject q = new BasicDBObject();
		q.put("job_id", jobId);
		q.put("page_id", pageId);
		DBObject sq = new BasicDBObject();
		sq.put("c_date", 1);
		return this.findAll(q, sq);
	}

	public void deleteByJobId(String jobId) {
		DBObject q = new BasicDBObject();
		q.put("job_id", jobId);
		this.delete(q);
	}

	public void deleteByPageId(String pageId) {
		DBObject q = new BasicDBObject();
		q.put("page_id", pageId);
		this.delete(q);
	}
}
