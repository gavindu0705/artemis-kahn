package com.artemis.kahn.dao.mongo.persistence;

import com.artemis.kahn.config.PropertiesBean;
import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public abstract class AbstractJpaCompatibleDao<E extends Serializable, K extends Serializable> extends AbstractEntityDao<E, K> {

    private String uri;
    private final String collectionName;

    private final Map<String, PropertyInfo> fieldMap;
    private final Map<String, PropertyInfo> propertyMap;
    private String idColumnName;
    private boolean idGeneratedValue;

    public static final String DEFAULT_MONGODB_URI = "_default";

    public AbstractJpaCompatibleDao() {
        // entity annotation
        javax.persistence.Entity entity = this.getEntityClass().getAnnotation(javax.persistence.Entity.class);
        if (null == entity) {
            throw new RuntimeException("no Entity annotation found of entity class " + this.getEntityClass().getName());
        }

        collectionName = entity.name();
        uri = PropertiesBean.getInstance().getStringValue("artemis.mongodb.uri." + collectionName);
        if (uri == null) {
            uri = PropertiesBean.getInstance().getStringValue("artemis.mongodb.uri._default");
        }

        if (null == uri) {
            throw new RuntimeException("error!!!!!! No configuration mongodb uri for " + collectionName + ", you can override it with the default configuration.");
        }

        // fields annotation
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(this.getEntityClass());
        Map<String, PropertyInfo> _fieldMap = new HashMap<String, PropertyInfo>(propertyDescriptors.length);
        Map<String, PropertyInfo> _propertyMap = new HashMap<String, PropertyInfo>(propertyDescriptors.length);
        boolean idFind = false;
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            Method readMethod = descriptor.getReadMethod();
            if (null != readMethod) {
                Column column = readMethod.getAnnotation(Column.class);
                if (null == column) {
                    continue;
                }
                Id id = readMethod.getAnnotation(Id.class);
                if (null != id) {
                    if (idFind) {
                        throw new RuntimeException("multi Id annotation found of entity class " + this.getEntityClass().getName());
                    }
                    idFind = true;
                    idColumnName = descriptor.getName();
                    GeneratedValue generatedValue = readMethod.getAnnotation(GeneratedValue.class);
                    idGeneratedValue = generatedValue != null;
                }
                Transient aTransient = readMethod.getAnnotation(Transient.class);
                PropertyInfo info = new PropertyInfo(descriptor, column, null != id, null != aTransient, this.getEntityClass());
                _propertyMap.put(descriptor.getName(), info);
                _fieldMap.put(column.name(), info);
            }
        }
        propertyMap = Collections.unmodifiableMap(_propertyMap);
        fieldMap = Collections.unmodifiableMap(_fieldMap);

    }


    public String getUri() {
        return uri;
    }

    public String getCollectionName() {
        return collectionName;
    }

    /**
     * 得到字段映射表，key为实体类的字段名（并非实际数据库的字段名）
     *
     * @return
     */
    public Map<String, PropertyInfo> getPropertyMap() {
        return propertyMap;
    }

    /**
     * 得到字段property的映射，property为实体类的字段名（并非实际数据库的字段名）
     *
     * @param property
     * @return
     */
    public PropertyInfo getPropertyInfoByProperty(String property) {
        PropertyInfo ret = propertyMap.get(property);
        if (null == ret) {
            throw new IllegalArgumentException("no property named [" + property + "] found of entity class "
                    + this.getEntityClass().getName());
        }
        return ret;
    }

    /**
     * 得到数据库字段映射表，key为数据库的字段名（并非实体类的字段名）
     *
     * @return
     */
    public Map<String, PropertyInfo> getFieldMap() {
        return fieldMap;
    }

//    /**
//     * 得到数据库字段field的映射，field为数据库的字段名（并非实体类的字段名）
//     *
//     * @param field
//     * @return
//     */
//    public PropertyInfo getPropertyInfoByField(String field) {
//        PropertyInfo ret = fieldMap.get(field);
//        if (null == ret) {
//            throw new IllegalArgumentException("no field named [" + field + "] found of table " + tableName);
//        }
//        return ret;
//    }

    /**
     * 得到id字段的字段名
     *
     * @return
     */
    public String getIdColumnName() {
        return idColumnName;
    }

    /**
     * id字段是否为自动生成
     *
     * @return
     */
    public boolean isIdGeneratedValue() {
        return idGeneratedValue;
    }

    /**
     * 实体类字段属性信息
     *
     * @author velna
     */
    public static class PropertyInfo {
        private final PropertyDescriptor propertyDescriptor;
        private final Column column;
        private final Class<?> clazz;
        private final boolean isId;
        private final boolean transients;

        public PropertyInfo(PropertyDescriptor propertyDescriptor, Column column, boolean isId, boolean transients, Class<?> clazz) {
            if (!column.name().toLowerCase().equals(column.name())) {
                // LOG.warn("name of column [" + propertyDescriptor.getName() +
                // "] must be lower cased of class " + clazz.getName()
                // + ", current is [" + column.name() + "]");
            }
            this.isId = isId;
            this.propertyDescriptor = propertyDescriptor;
            this.column = column;
            this.clazz = clazz;
            this.transients = transients;
        }

        /**
         * 得到字段的描述信息
         *
         * @return
         */
        public PropertyDescriptor getPropertyDescriptor() {
            return propertyDescriptor;
        }

        /**
         * 得到Column注解
         *
         * @return
         */
        public Column getColumn() {
            return column;
        }

        /**
         * 是否为id字段
         *
         * @return
         */
        public boolean isID() {
            return isId;
        }

        /**
         * 得到所属的实体类
         *
         * @return
         */
        public Class<?> getClazz() {
            return clazz;
        }

        /**
         * 是否为瞬态的
         *
         * @return
         */
        public boolean isTransient() {
            return transients;
        }

    }
}
