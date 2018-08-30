package com.artemis.kahn.dao.mongo.persistence;


import java.io.Serializable;

public interface MongoEntity extends Serializable {
	public final static String ID = "_id";
	
	String getId();

	void setId(String id);
}
