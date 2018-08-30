package com.artemis.kahn.dao.mongo.persistence;

import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class MongoDaoImpl<E extends MongoEntity> extends AbstractMongoDao<E> {

    private final static Logger LOG = LoggerFactory.getLogger(MongoDaoImpl.class);

    public MongoDaoImpl() {
        super();
    }

    @Override
    protected DBObject internalFindOne(DBObject query, DBObject fields) {
        long beginMills = System.currentTimeMillis();
        try {
            return getDbCollection().findOne(query, fields);
        } finally {
            performanceLogger(beginMills, "find", query, null);
        }

    }

    @Override
    protected void internalUpdate(DBObject query, DBObject data, boolean upsert, boolean multi) {
        long beginMills = System.currentTimeMillis();
        try {
            getDbCollection().update(query, data, upsert, multi, WriteConcern.SAFE);
        } finally {
            performanceLogger(beginMills, "update", query, data);
        }
    }

    @Override
    protected void internalSave(DBObject data) {
        long beginMills = System.currentTimeMillis();
        try {
            getDbCollection().save(data, WriteConcern.SAFE);
        } finally {
            performanceLogger(beginMills, "save", data);
        }
    }

    protected void internalSave(List<DBObject> datas) {
        // long beginMills = System.currentTimeMillis();
        // try {
//		 getDbCollection().insert(datas, WriteConcern.SAFE);
        // } finally {
        // performanceLogger(beginMills, "save", datas);
        // }

        if (datas != null) {
            for (DBObject data : datas) {
                this.internalSave(data);
            }
        }

    }

    protected void internalInsert(List<DBObject> datas) {
        long beginMills = System.currentTimeMillis();
        try {
            getDbCollection().insert(datas);
        } finally {
            performanceLogger(beginMills, "save", datas);
        }

    }

    @Override
    protected long internalCount(DBObject query) {
        long beginMills = System.currentTimeMillis();
        try {
            return getDbCollection().getCount(query);
        } finally {
            performanceLogger(beginMills, "count", query, null);
        }
    }

    @Override
    protected void internalRemove(DBObject query) {
        long beginMills = System.currentTimeMillis();
        try {
            getDbCollection().remove(query, WriteConcern.SAFE);
        } finally {
            performanceLogger(beginMills, "remove", query, null);
        }
    }

    @Override
    protected List<E> find(DBObject query, DBObject fields, DBObject sort, int skip, int limit) {
        long beginMills = System.currentTimeMillis();
        try {
            List<DBObject> objects = getDbCollection().find(query, fields).sort(sort).skip(skip).batchSize(500).limit(limit).toArray();
            List<E> ret = new ArrayList<E>(objects.size());
            for (DBObject obj : objects) {
                ret.add(this.dbObjectToEntity(obj));
            }
            return ret;
        } finally {
            performanceLogger(beginMills, "find", query, sort, skip, limit);
        }
    }

    protected Iterator<E> iterator(DBObject query, DBObject fields, DBObject sort, int skip, int limit) {
        long beginMills = System.currentTimeMillis();
        try {
            final Iterator<DBObject> iter = getDbCollection().find(query, fields).sort(sort).skip(skip).limit(limit).iterator();
            return new Iterator<E>() {
                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public E next() {
                    return dbObjectToEntity(iter.next());
                }

                @Override
                public void remove() {

                }
            };
        } finally {
            performanceLogger(beginMills, "iterator", query, sort, skip, limit);
        }
    }

    private void performanceLogger(long beginMills, String operation, Object data) {
        performanceLogger(beginMills, operation, null, null, -1, -1, data);
    }

    private void performanceLogger(long beginMills, String operation, DBObject query, Object data) {
        performanceLogger(beginMills, operation, query, null, -1, -1, data);
    }

    private void performanceLogger(long beginMills, String operation, DBObject query, DBObject sort, int skip, int limit) {
        performanceLogger(beginMills, operation, query, sort, skip, limit, null);
    }

    @SuppressWarnings("rawtypes")
    private void performanceLogger(long beginMills, String operation, DBObject query, DBObject sort, int skip, int limit,
                                   Object data) {
        long diffMills = System.currentTimeMillis() - beginMills;
        StringBuilder builder = new StringBuilder();
        builder.append("operation:").append(operation);
        builder.append("\ttime:").append(diffMills);
        builder.append("\tdb:").append(getDbCollection().getFullName());
        if (query != null) {
            builder.append("\tquery:").append(query.toString());
        }
        if (sort != null) {
            builder.append("\tsort:").append(sort.toString());
        }

        if (skip > -1) {
            builder.append("\tskip:").append(skip);
        }

        if (limit > -1) {
            builder.append("\tlimit:").append(limit);
        }

        if (data != null) {
            if (data instanceof List) {
                builder.append("\tdata:[");
                List list = (List) data;
                for (int i = 0; i < list.size() && i < 3; i++) {
                    if (list.get(i).toString().length() > 200) {
                        builder.append(list.get(i).toString().substring(0, 200)).append("...");
                    } else {
                        builder.append(list.get(i).toString());
                    }
                }
                builder.append("]");
                if (list.size() > 3) {
                    builder.append("...").append(list.size()).append(" objects");
                }
            } else {
                if (data.toString().length() > 200) {
                    builder.append("\tdata:").append(data.toString().substring(0, 200)).append("...");
                } else {
                    builder.append("\tdata:").append(data.toString());
                }
            }
        }

//		if (diffMills > 500) {
//			LOG.performance(Level.ERROR, builder.toString());
//		} else if (diffMills > 300) {
//			LOG.performance(Level.WARN, builder.toString());
//		} else if (diffMills > 100) {
//			LOG.performance(Level.INFO, builder.toString());
//		} else {
//			LOG.performance(Level.DEBUG, builder.toString());
//		}
    }
}
