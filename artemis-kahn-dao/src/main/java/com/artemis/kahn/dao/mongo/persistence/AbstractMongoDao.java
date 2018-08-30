package com.artemis.kahn.dao.mongo.persistence;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.types.ObjectId;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMongoDao<E extends MongoEntity> extends AbstractJpaCompatibleDao<E, String> implements MongoDao<E> {
    public static final String ID = "_id";

    protected final DBObject fullFields;
    private DBCollection dbCollection;



    public AbstractMongoDao() {
        super();
        fullFields = new BasicDBObject();
        Map<String, PropertyInfo> propertyMap = this.getPropertyMap();
        for (Map.Entry<String, PropertyInfo> entry : propertyMap.entrySet()) {
            PropertyInfo info = entry.getValue();
            if (!info.isTransient()) {
                fullFields.put(info.getColumn().name(), 1);
            }
        }

//        String currentMongodbUri = propertiesBean.getStringValue("artemis.mongodb.datasource." + getCollectionName() + ".uri");
//        if (currentMongodbUri == null) {
//            currentMongodbUri = DEFAULT_MONGODB_URI;
//        }


//        String host = propertiesBean.getStringValue("artemis.mongodb.datasource." + tableName + ".host");
//        Integer port = propertiesBean.getIntegerValue("artemis.mongodb.datasource." + tableName + ".port");
//        String authname = propertiesBean.getStringValue("artemis.mongodb.datasource." + tableName + ".authname");
//        String authpwd = propertiesBean.getStringValue("artemis.mongodb.datasource." + tableName + ".authpwd");

//        MongoClientOptions mongoClientOptions = MongoClientOptions.builder()
//                .retryWrites(true)
//                .connectionsPerHost(4096)
//                .threadsAllowedToBlockForConnectionMultiplier(1024)
//                .build();
        MongoClientURI clientURI = new MongoClientURI(getUri());
        MongoClient mongoClient = new MongoClient(clientURI);
        DB mongoDatabase = mongoClient.getDB(clientURI.getDatabase());
        dbCollection = mongoDatabase.getCollection(getCollectionName());
    }

    protected abstract DBObject internalFindOne(DBObject query, DBObject fields);

    protected abstract void internalUpdate(DBObject query, DBObject data, boolean upsert, boolean multi);

    protected abstract void internalSave(DBObject data);

    protected abstract void internalSave(List<DBObject> datas);

    protected abstract void internalInsert(List<DBObject> datas);

    protected abstract long internalCount(DBObject query);

    protected abstract void internalRemove(DBObject query);

    protected abstract List<E> find(DBObject query, DBObject fields, DBObject sort, int skip, int limit);

    protected abstract Iterator<E> iterator(DBObject query, DBObject fields, DBObject sort, int skip, int limit);

    public DBCollection getDbCollection() {
        return dbCollection;
    }

    @Override
    public void delete(DBObject q) {
        internalRemove(q);
    }

    @Override
    public void deleteById(String id) {
        if (this.isIdGeneratedValue()) {
            internalRemove(new BasicDBObject(ID, new ObjectId(id)));
        } else {
            internalRemove(new BasicDBObject(ID, id));
        }

    }

    @Override
    public void deleteByIds(List<String> idList) {
        for (String id : idList) {
            this.deleteById(id);
        }
    }

    @Override
    public List<E> findAll() {
        return findAll(new BasicDBObject());
    }

    @Override
    public List<E> findAll(DBObject q) {
        return findAll(q, Integer.MAX_VALUE);
    }

    @Override
    public List<E> findAll(DBObject q, int maxResult) {
        return findAll(q, 0, maxResult);
    }

    @Override
    public List<E> findAll(DBObject q, int firstResult, int maxResult) {
        return find(q, fullFields, null, 0, maxResult);
    }

    @Override
    public List<E> findAll(DBObject q, DBObject sq) {
        return findAll(q, sq, Integer.MAX_VALUE);
    }

    @Override
    public List<E> findAll(DBObject q, DBObject sq, int maxResult) {
        return findAll(q, sq, 0, maxResult);
    }

    @Override
    public List<E> findAll(DBObject q, DBObject sq, int firstResult, int maxResult) {
        return find(q, fullFields, sq, firstResult, maxResult);
    }

    @Override
    public Iterator<E> iteratorAll(DBObject q) {
        return iteratorAll(q, null);
    }

    @Override
    public Iterator<E> iteratorAll(DBObject q, DBObject sq) {
        return iteratorAll(q, sq, 0, Integer.MAX_VALUE);
    }

    @Override
    public Iterator<E> iteratorAll(DBObject q, DBObject sq, int firstResult, int maxResult) {
        return iteratorAll(q, sq, firstResult, maxResult, null);
    }

    @Override
    public Iterator<E> iteratorAll(DBObject q, DBObject sq, String[] fields) {
        return iteratorAll(q, sq, 0, Integer.MAX_VALUE, fields);
    }

    @Override
    public Iterator<E> iteratorAll(DBObject q, DBObject sq, int firstResult, int maxResult, String[] fields) {
        DBObject fds = new BasicDBObject();
        if (fields == null) {
            fds.putAll(fullFields);
        } else {
            for (int i = 0; i < fields.length; i++) {
                fds.put(fields[i], 1);
            }
        }
        return iterator(q, fds, sq, firstResult, maxResult);
    }

    @Override
    public List<E> findAllByIds(List<String> idList) {
        List<E> ret = new ArrayList<E>(idList.size());
        for (String id : idList) {
            ret.add(this.findById(id));
        }
        return ret;
    }

    @Override
    public E findById(String id) {
        if (this.isIdGeneratedValue()) {
            return this.dbObjectToEntity(internalFindOne(new BasicDBObject(ID, new ObjectId(id)), fullFields));
        }
        return this.dbObjectToEntity(internalFindOne(new BasicDBObject(ID, id), fullFields));
    }

    @Override
    public E findById(String id, String[] fields) {
        DBObject fieldsObj = new BasicDBObject();
        if (fields != null && fields.length > 0) {
            for (String fs : fields) {
                fieldsObj.put(fs, 1);
            }
        } else {
            fieldsObj = fullFields;
        }

        if (this.isIdGeneratedValue()) {
            return this.dbObjectToEntity(internalFindOne(new BasicDBObject(ID, new ObjectId(id)), fieldsObj));
        }
        return this.dbObjectToEntity(internalFindOne(new BasicDBObject(ID, id), fieldsObj));
    }

    @Override
    public long findCount() {
        return internalCount(new BasicDBObject());
    }

    @Override
    public long findCount(DBObject q) {
        return internalCount(q);
    }

    @Override
    public E findOne(DBObject q) {
        return this.dbObjectToEntity(this.internalFindOne(q, fullFields));
    }

    @Override
    public void save(E entity) {
        DBObject dbObject = this.entityToDBObject(entity);
        this.internalSave(dbObject);
        entity.setId(toEntityId(dbObject.get(ID)));
    }

    @Override
    public List<Object> distinct(DBObject q, String key) {
        return getDbCollection().distinct(key, q);
    }

    @Override
    public void saveList(List<E> entities) {
        List<DBObject> datas = new ArrayList<DBObject>(entities.size());
        for (E e : entities) {
            datas.add(this.entityToDBObject(e));
        }
        this.internalSave(datas);
    }

    @Override
    public void insertList(List<E> entities) {
        List<DBObject> datas = new ArrayList<DBObject>(entities.size());
        for (E e : entities) {
            datas.add(this.entityToDBObject(e));
        }
        this.internalInsert(datas);
    }

    @Override
    public void update(DBObject data, DBObject q) {
        if (null == data || data.keySet().size() == 0) {
            return;
        }

        DBObject setDbObject = new BasicDBObject();
        for (String key : data.keySet()) {
            setDbObject.put(key, data.get(key));
        }

        this.internalUpdate(q, new BasicDBObject("$set", setDbObject), false, true);
    }

    @Override
    public void updateById(DBObject data, String id) {
        if (this.isIdGeneratedValue()) {
            this.update(data, new BasicDBObject(ID, new ObjectId(id)));
        } else {
            this.update(data, new BasicDBObject(ID, id));
        }
    }

    protected E dbObjectToEntity(DBObject dbObject) {
        if (null == dbObject) {
            return null;
        }
        E entity;
        try {
            entity = this.getEntityClass().newInstance();
            Set<String> keySet = dbObject.keySet();
            Map<String, PropertyInfo> fieldMap = this.getFieldMap();
            for (String key : keySet) {
                PropertyInfo propertyInfo = fieldMap.get(key);
                if (null == propertyInfo) {
                    continue;
                }
                PropertyDescriptor descriptor = propertyInfo.getPropertyDescriptor();
                Method writeMethod = descriptor.getWriteMethod();
                Object value = dbObject.get(key);
                if (value instanceof ObjectId) {
                    value = ((ObjectId) value).toString();
                }
                try {
                    writeMethod.invoke(entity, value);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Error set value to " + value + "[" + value.getClass() + "] of field "
                            + descriptor.getName() + "[" + descriptor.getPropertyType() + "]", e);
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected DBObject entityToDBObject(E entity) {
        if (null == entity) {
            return null;
        }
        DBObject dbObject = new BasicDBObject();
        Map<String, PropertyInfo> propertyMap = this.getPropertyMap();
        for (Map.Entry<String, PropertyInfo> entry : propertyMap.entrySet()) {
            PropertyInfo propertyInfo = entry.getValue();
            if (propertyInfo.isTransient()) {
                continue;
            }
            Method readMethod = propertyInfo.getPropertyDescriptor().getReadMethod();
            Object value;
            try {
                value = readMethod.invoke(entity, (Object[]) null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (propertyInfo.isID()) {
                value = toMongoId(value);
            }
            if (null != value) {
                dbObject.put(propertyInfo.getColumn().name(), value);
            }
        }
        return dbObject;
    }

    protected String toEntityId(Object id) {
        if (this.isIdGeneratedValue()) {
            ObjectId objectId = (ObjectId) id;
            return objectId.toString();
        } else {
            return (String) id;
        }
    }

    @SuppressWarnings("unchecked")
    protected Object toMongoId(Object id) {
        if (null == id) {
            return null;
        }
        if (id instanceof List) {
            List<Object> idList = (List<Object>) id;
            List<Object> idObjectList = new ArrayList<Object>(idList.size());
            for (Object _id : idList) {
                idObjectList.add(toMongoId(_id));
            }
            return idObjectList;
        }
        if (!(id instanceof String)) {
            throw new IllegalArgumentException("id must be instanceof " + String.class.getName() + ", but was "
                    + id.getClass().getName() + " of class " + this.getEntityClass().getName());
        }
        if (this.isIdGeneratedValue()) {
            return new ObjectId((String) id);
        } else {
            return id;
        }
    }

    @SuppressWarnings("unchecked")
    protected DBObject dataToDBObject(Map<String, Object> data) {
        if (null == data) {
            return null;
        }
        DBObject ret = new BasicDBObject();
        DBObject setDbObject = new BasicDBObject();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            if (key.equals("$set")) {
                DBObject obj = doDataToDBObject((Map<String, Object>) entry.getValue());
                setDbObject.putAll(obj);
            } else if (key.startsWith("$")) {
                ret.put(key, doDataToDBObject((Map<String, Object>) entry.getValue()));
            } else {
                PropertyInfo propertyInfo = getPropertyInfoByProperty(key);
                if (propertyInfo.isID()) {
                    setDbObject.put(propertyInfo.getColumn().name(), toMongoId(entry.getValue()));
                } else {
                    setDbObject.put(propertyInfo.getColumn().name(), entry.getValue());
                }
            }
        }
        if (setDbObject.keySet().size() > 0) {
            ret.put("$set", setDbObject);
        }
        if (ret.keySet().size() > 0) {
            return ret;
        } else {
            return null;
        }
    }

    protected DBObject doDataToDBObject(Map<String, Object> data) {
        if (null == data) {
            return null;
        }
        DBObject ret = new BasicDBObject();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            PropertyInfo propertyInfo = getPropertyInfoByProperty(key);
            if (propertyInfo.isID()) {
                ret.put(propertyInfo.getColumn().name(), toMongoId(entry.getValue()));
            } else {
                ret.put(propertyInfo.getColumn().name(), entry.getValue());
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> dbObjectToMap(DBObject dbObject) {
        if (null == dbObject) {
            return null;
        }
        Map<Object, Object> map = dbObject.toMap();
        if (null == map) {
            return null;
        }
        Map<String, Object> ret = new HashMap<String, Object>(map.size());
        for (Object key : map.keySet()) {
            if (ID.equals(key)) {
                ret.put(this.getIdColumnName(), toEntityId(map.get(key)));
            } else if (key instanceof String) {
                ret.put((String) key, map.get(key));
            } else {
                throw new RuntimeException("invalid type of key [" + key.getClass().getName() + "] of class "
                        + this.getEntityClass().getName());
            }
        }
        return ret;
    }

    @Override
    public List<E> findAll(DBObject q, String[] fields) {
        return findAll(q, Integer.MAX_VALUE, fields);
    }

    @Override
    public List<E> findAll(DBObject q, int maxResult, String[] fields) {
        return findAll(q, 0, maxResult, fields);
    }

    @Override
    public List<E> findAll(DBObject q, int firstResult, int maxResult, String[] fields) {
        return findAll(q, null, firstResult, maxResult, fields);
    }

    @Override
    public List<E> findAll(DBObject q, DBObject sq, String[] fields) {
        return findAll(q, sq, Integer.MAX_VALUE, fields);
    }

    @Override
    public List<E> findAll(DBObject q, DBObject sq, int maxResult, String[] fields) {
        return findAll(q, sq, 0, maxResult, fields);
    }

    @Override
    public List<E> findAll(DBObject q, DBObject sq, int firstResult, int maxResult, String[] fields) {
        DBObject fds = new BasicDBObject();
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                fds.put(fields[i], 1);
            }
        }
        return find(q, fds, sq, firstResult, maxResult);
    }

    @Override
    public E findOne(DBObject q, String[] fields) {
        DBObject fds = new BasicDBObject();
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                fds.put(fields[i], 1);
            }
        }
        return this.dbObjectToEntity(this.internalFindOne(q, fds));
    }

}