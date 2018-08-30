package com.artemis.kahn.dao.mongo.repo;


import com.artemis.kahn.dao.mongo.entity.FileData;
import com.artemis.kahn.dao.mongo.persistence.MongoDao;
import com.artemis.kahn.dao.mongo.persistence.MongoDaoImpl;
import org.springframework.stereotype.Service;

/**
 * file_data是capped mongo
 *
 * @author dxy
 */

@Service
public class FileDataDao extends MongoDaoImpl<FileData> implements MongoDao<FileData> {


}
