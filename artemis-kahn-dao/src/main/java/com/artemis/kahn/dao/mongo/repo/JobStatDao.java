package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.JobStat;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JobStatDao extends MongoDaoImpl<JobStat> implements MongoDao<JobStat> {

	public JobStat findJobStat(String jobId, String sessionId) {
		DBObject q = new BasicDBObject();
		q.put("job_id", jobId);
		q.put("session_id", sessionId);
		return this.findOne(q);
	}

	public JobStat findLastestJobStat(String jobId) {
		DBObject q = new BasicDBObject();
		q.put("job_id", jobId);
		List<JobStat> list = this.findAll(q, new BasicDBObject("c_date", -1), 1);
		if(list != null) {
			return list.get(0);
		}

		return null;
	}


}
