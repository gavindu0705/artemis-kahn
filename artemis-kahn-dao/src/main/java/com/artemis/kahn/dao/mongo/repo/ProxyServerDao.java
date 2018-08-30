package com.artemis.kahn.dao.mongo.repo;

import com.artemis.kahn.dao.mongo.entity.ProxyServer;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class ProxyServerDao extends MongoDaoImpl<ProxyServer> implements MongoDao<ProxyServer> {

    public void updateToRestarting(String id) {
        DBObject data = new BasicDBObject();
        data.put("status", ProxyServer.StatusEnum.RESTART.getStatus());
        this.updateById(data, id);
    }

    public void updateToRestarted(String id, int status, String publicIp) {
        DBObject data = new BasicDBObject();
        data.put("status", status);
        data.put("public_ip", publicIp);
        data.put("restart_date", new Date());
        this.updateById(data, id);
    }

    public void updateToError(String id) {
        DBObject data = new BasicDBObject();
        data.put("status", ProxyServer.StatusEnum.ERROR.getStatus());
        this.updateById(data, id);
    }

    public void updateByQuery(DBObject q, ProxyServer.StatusEnum statusEnum) {
        DBObject data = new BasicDBObject();
        data.put("status", statusEnum.getStatus());
        this.update(data, q);
    }
}
