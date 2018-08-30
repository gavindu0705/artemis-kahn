package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.Footprint;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;


@Service
public class FootprintDao extends MongoDaoImpl<Footprint> implements MongoDao<Footprint> {

    public boolean exists(String url, String jobId, String sessionId) {
        Footprint obj = findById(idGenerator(url, jobId, sessionId));
        if (obj != null) {
            return true;
        }
        return false;
    }

    public void print(String url, String jobId, String sessionId) {
        Footprint entity = new Footprint();
        entity.setId(idGenerator(url, jobId, sessionId));
        this.save(entity);
    }

    private String idGenerator(String url, String jobId, String sessionId) {
        String key = url + "#" + jobId + "#" + sessionId;
        return DigestUtils.md5Hex(key) + Math.abs(key.hashCode());
    }

}
