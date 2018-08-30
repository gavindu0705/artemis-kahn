package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.Seed;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;
import com.artemis.kahn.dao.mongo.entity.Seed.StatusEnum;

import java.util.Iterator;
import java.util.List;

@Service
public class SeedDao extends MongoDaoImpl<Seed> implements MongoDao<Seed> {
    public long getCount(String jobId) {
        return findCount(new BasicDBObject("job_id", jobId));
    }

    public long getCount(String jobId, StatusEnum status) {
        return findCount(new BasicDBObject("job_id", jobId).append("status", status.getStatus()));
    }

    public List<Seed> findSeedByJobId(String jobId, int skip, int limit) {
        return find(new BasicDBObject("job_id", jobId), null, new BasicDBObject("c_date", 1), skip, limit);
    }

    public List<Seed> findSeedByJobId(String jobId, StatusEnum status, int skip, int limit) {
        return find(new BasicDBObject("job_id", jobId).append("status", status.getStatus()), null,
                new BasicDBObject("c_date", 1), skip, limit);
    }


    public List<Seed> findAllSeedByJobId(String jobId) {
        return findAll(new BasicDBObject("job_id", jobId), new BasicDBObject("c_date", 1));
    }

    public List<Seed> findAllSeedByJobId(String jobId, StatusEnum status) {
        return findAll(new BasicDBObject("job_id", jobId).append("status", status.getStatus()), new BasicDBObject("c_date", 1));
    }

    public Iterator<Seed> iteratorSeedByJobId(String jobId, StatusEnum status) {
        return iteratorAll(new BasicDBObject("job_id", jobId).append("status", status.getStatus()), new BasicDBObject("c_date", 1));
    }

    public Seed findSeedByUrl(String jobId, String url) {
        return findOne(new BasicDBObject("job_id", jobId).append("url", url));
    }

    public void updateSeedStatus(String jobId, StatusEnum status) {
        this.update(new BasicDBObject("status", status.getStatus()), new BasicDBObject("job_id", jobId));
    }

    public void updateSeedStatus(String jobId, String url, StatusEnum status) {
        this.update(new BasicDBObject("status", status.getStatus()), new BasicDBObject("job_id", jobId).append("url", url));
    }

    public void updateById(String id, StatusEnum status) {
        this.updateById(new BasicDBObject("status", status.getStatus()), id);
    }

    public void deleteByJobId(String jobId) {
        DBObject q = new BasicDBObject();
        q.put("job_id", jobId);
        this.delete(q);
    }
}
